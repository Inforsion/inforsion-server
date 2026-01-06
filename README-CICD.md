# CI/CD 빠른 시작 가이드

## 🚀 5분 안에 시작하기

### 1. Makefile 테스트
```bash
# 명령어 확인
make help

# 빌드 테스트
make build
```

### 2. Jenkins 실행
```bash
# Jenkins 시작
make jenkins

# 초기 비밀번호 확인 (30초 후)
docker exec inforsion-jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# 브라우저 접속: http://localhost:8081
```

### 3. Jenkins 초기 설정
1. 비밀번호 입력
2. "Install suggested plugins" 선택
3. 관리자 계정 생성 (admin / 원하는비밀번호)
4. Jenkins URL: http://localhost:8081
5. Start using Jenkins!

### 4. Docker Hub Credentials 등록
- Manage Jenkins → Credentials → System → Global credentials → Add
- Kind: `Username with password`
- Username: Docker Hub 사용자명
- Password: Docker Hub Access Token
- ID: `docker-hub-credentials`

### 5. Jenkinsfile 수정
```bash
# Jenkinsfile 11번째 줄 수정
DOCKER_IMAGE = 'YOUR_DOCKERHUB_USERNAME/inforsion-server'
              ↓
DOCKER_IMAGE = '본인계정/inforsion-server'
```

### 6. Pipeline Job 생성
1. New Item → 이름: `inforsion-pipeline`
2. Type: Pipeline
3. Pipeline:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: 본인 GitHub 저장소 URL
   - Branch: `*/main` (또는 현재 브랜치)
   - Script Path: `Jenkinsfile`
4. Save → **Build Now!**

---

## 📋 주요 명령어

```bash
# 개발
make test           # 테스트
make build          # 빌드
make docker         # Docker 이미지 빌드

# Jenkins
make jenkins        # Jenkins 시작
make jenkins-stop   # Jenkins 중지
make jenkins-logs   # Jenkins 로그

# 실행
make run            # 전체 스택 실행
make status         # 상태 확인
make logs           # 로그 확인
make stop           # 중지

# 정리
make clean          # 빌드 파일 정리
make clean-all      # 전체 정리
```

---

## 📂 핵심 파일

```
inforsion-server/
├── Makefile                      # 빌드 자동화
├── Jenkinsfile                   # 실제 파이프라인
├── Jenkinsfile.test              # 테스트용
├── docker-compose.yml            # 애플리케이션
└── docker-compose.jenkins.yml    # Jenkins
```

---

## 🔧 파이프라인 단계

```
1. Checkout       → GitHub에서 코드 가져오기
2. Test           → JUnit 테스트 실행
3. Build          → Gradle 빌드
4. Docker Build   → Docker 이미지 생성
5. Docker Push    → Docker Hub 업로드
6. Cleanup        → 정리
```

**소요 시간**: 약 7-10분

---

## ❓ 트러블슈팅

### Jenkins가 시작되지 않음
```bash
# Docker 확인
docker ps

# 재시작
make jenkins-stop
make jenkins
```

### Docker Hub 푸시 실패
- Credentials ID 확인: `docker-hub-credentials`
- Docker Hub Access Token 재생성
- Credentials 다시 등록

### 빌드 실패
```bash
# 로컬에서 테스트
make test
make build

# Jenkins 로그 확인
make jenkins-logs
```

---

## 🎯 GitHub 연동 (선택)

### Webhook 설정
1. GitHub Repository → Settings → Webhooks → Add webhook
2. Payload URL: `http://YOUR_JENKINS_URL:8081/github-webhook/`
3. Content type: `application/json`
4. Events: `Just the push event`

### Jenkins Job 설정
- Build Triggers → ✅ `GitHub hook trigger for GITScm polling`

### 로컬 테스트용 (ngrok)
```bash
# ngrok 설치
brew install ngrok

# 실행
ngrok http 8081

# 출력된 URL을 GitHub Webhook에 사용
https://abc123.ngrok.io/github-webhook/
```

---

## ✅ 완료 체크리스트

- [ ] Makefile 테스트 성공
- [ ] Jenkins 실행 성공
- [ ] Credentials 등록 완료
- [ ] Jenkinsfile 수정 완료
- [ ] 첫 빌드 성공
- [ ] Docker Hub에 이미지 업로드 확인

---

**완료! 🎉**

이제 `git push`하면 자동으로 빌드/배포됩니다!