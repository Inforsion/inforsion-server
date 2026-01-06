# ============================================
# Inforsion Server - 빌드 자동화
# ============================================

.PHONY: help test build docker run clean status logs

# 기본 명령어 (make 입력 시)
help:
	@echo ""
	@echo "🎯 Inforsion Server - 사용 가능한 명령어"
	@echo "=========================================="
	@echo ""
	@echo "  📋 개발 명령어:"
	@echo "    make test        - 테스트 실행"
	@echo "    make build       - Gradle 빌드"
	@echo "    make docker      - Docker 이미지 빌드"
	@echo ""
	@echo "  🚀 실행 명령어:"
	@echo "    make run         - 전체 스택 실행 (DB + App)"
	@echo "    make app         - 애플리케이션만 실행"
	@echo "    make stop        - 전체 중지"
	@echo ""
	@echo "  📊 상태 확인:"
	@echo "    make status      - 컨테이너 상태 확인"
	@echo "    make logs        - 애플리케이션 로그 확인"
	@echo "    make health      - 헬스 체크"
	@echo ""
	@echo "  🧹 정리 명령어:"
	@echo "    make clean       - 빌드 파일 정리"
	@echo "    make clean-all   - 전체 정리 (컨테이너 + 이미지)"
	@echo ""

# ============================================
# 개발 명령어
# ============================================

# 테스트 실행
test:
	@echo "🧪 테스트 실행 중..."
	@./gradlew test --no-daemon
	@echo "✅ 테스트 완료!"

# Gradle 빌드
build:
	@echo "🔨 Gradle 빌드 중..."
	@./gradlew clean build -x test --no-daemon
	@echo "✅ 빌드 완료!"
	@echo "📦 JAR 파일: build/libs/inforsion-server-0.0.1-SNAPSHOT.jar"

# Docker 이미지 빌드
docker:
	@echo "🐳 Docker 이미지 빌드 중..."
	@docker build -t inforsion/server:local .
	@echo "✅ Docker 이미지 빌드 완료!"
	@docker images | grep inforsion

# 전체 빌드 (테스트 + 빌드 + Docker)
all: test build docker
	@echo "✅ 전체 빌드 완료!"

# ============================================
# 실행 명령어
# ============================================

# 전체 스택 실행 (DB + App)
run:
	@echo "🚀 전체 스택 실행 중..."
	@docker-compose up -d
	@echo "✅ 실행 완료!"
	@echo ""
	@make status
	@echo ""
	@echo "📍 접속 정보:"
	@echo "  - API: http://localhost:8080"
	@echo "  - Swagger: http://localhost:8080/swagger-ui.html"
	@echo "  - Health: http://localhost:8080/actuator/health"

# 애플리케이션만 재시작
app:
	@echo "🔄 애플리케이션 재시작 중..."
	@docker-compose restart app
	@echo "✅ 재시작 완료!"

# 전체 중지
stop:
	@echo "🛑 전체 스택 중지 중..."
	@docker-compose down
	@echo "✅ 중지 완료!"

# ============================================
# 상태 확인
# ============================================

# 컨테이너 상태 확인
status:
	@echo "📊 컨테이너 상태:"
	@docker-compose ps

# 로그 확인
logs:
	@echo "📋 애플리케이션 로그:"
	@docker-compose logs -f --tail=50 app

# 헬스 체크
health:
	@echo "🏥 헬스 체크 중..."
	@curl -f http://localhost:8080/actuator/health 2>/dev/null && echo "\n✅ 서버 정상 작동 중!" || echo "\n❌ 서버 응답 없음"

# ============================================
# Jenkins 관련
# ============================================

# Jenkins 실행
jenkins:
	@echo "🤖 Jenkins 실행 중..."
	@docker-compose -f docker-compose.jenkins.yml up -d
	@sleep 5
	@echo "✅ Jenkins 실행 완료!"
	@echo ""
	@echo "📍 Jenkins 접속: http://localhost:8081"
	@echo "🔑 초기 비밀번호 확인:"
	@docker exec inforsion-jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>/dev/null || echo "  (Jenkins 시작 중... 잠시 후 다시 시도하세요)"

# Jenkins 중지
jenkins-stop:
	@echo "🛑 Jenkins 중지 중..."
	@docker-compose -f docker-compose.jenkins.yml down
	@echo "✅ Jenkins 중지 완료!"

# Jenkins 로그
jenkins-logs:
	@echo "📋 Jenkins 로그:"
	@docker-compose -f docker-compose.jenkins.yml logs -f jenkins

# ============================================
# 정리 명령어
# ============================================

# 빌드 파일 정리
clean:
	@echo "🧹 빌드 파일 정리 중..."
	@./gradlew clean
	@echo "✅ 정리 완료!"

# 전체 정리 (컨테이너 + 이미지)
clean-all:
	@echo "🧹 전체 정리 중..."
	@docker-compose down -v
	@docker-compose -f docker-compose.jenkins.yml down -v
	@docker image prune -af
	@./gradlew clean
	@echo "✅ 전체 정리 완료!"

# ============================================
# 유틸리티
# ============================================

# DB 초기화 (주의!)
db-reset:
	@echo "⚠️  데이터베이스 초기화 중... (모든 데이터 삭제!)"
	@docker-compose down -v
	@docker-compose up -d mysql mongodb redis
	@echo "✅ 데이터베이스 초기화 완료!"

# 환경 변수 확인
env-check:
	@echo "🔍 환경 확인:"
	@echo "Java: $$(java -version 2>&1 | head -1)"
	@echo "Gradle: $$(./gradlew --version | grep Gradle)"
	@echo "Docker: $$(docker --version)"
	@echo "Docker Compose: $$(docker-compose --version)"