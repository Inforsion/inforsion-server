// ============================================
// Inforsion Server - Jenkins CI/CD Pipeline
// ============================================
//
// 이 파이프라인은 다음을 수행합니다:
// 1. 코드 체크아웃
// 2. 테스트 실행
// 3. Gradle 빌드
// 4. Docker 이미지 빌드
// 5. Docker Hub에 푸시
// 6. (선택) 서버 배포
//
// 설정 필요:
// - Jenkins Credentials: docker-hub-credentials
// - Docker Hub 계정
//

pipeline {
    agent any

    environment {
        // Docker 이미지 설정
        // 🔧 TODO: 본인의 Docker Hub 사용자명으로 변경!
        DOCKER_IMAGE = 'YOUR_DOCKERHUB_USERNAME/inforsion-server'
        DOCKER_TAG = "${BUILD_NUMBER}"

        // Docker Hub Credentials (Jenkins에 등록 필요)
        DOCKER_CREDENTIALS = credentials('docker-hub-credentials')

        // 프로젝트 경로
        WORKSPACE_PATH = '/workspace'
    }

    stages {
        stage('📥 Checkout') {
            steps {
                echo '============================================'
                echo '  Inforsion Server CI/CD Pipeline'
                echo '============================================'
                echo ''
                echo '📥 Git 코드 체크아웃 중...'
                checkout scm
                echo '✅ 체크아웃 완료'
            }
        }

        stage('🔍 Environment Check') {
            steps {
                echo '🔍 환경 확인 중...'
                sh '''
                    echo "Java Version:"
                    java -version

                    echo "\nDocker Version:"
                    docker --version

                    echo "\nDocker Compose Version:"
                    docker-compose --version

                    echo "\nGradle Version:"
                    cd ${WORKSPACE_PATH}
                    chmod +x gradlew
                    ./gradlew --version
                '''
                echo '✅ 환경 확인 완료'
            }
        }

        stage('🧪 Test') {
            steps {
                echo '🧪 테스트 실행 중...'
                sh '''
                    cd ${WORKSPACE_PATH}
                    ./gradlew test --no-daemon
                '''
                echo '✅ 테스트 완료'
            }
            post {
                always {
                    // 테스트 결과 수집
                    junit '**/build/test-results/test/*.xml'

                    // HTML 리포트 게시
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report'
                    ])
                }
            }
        }

        stage('🔨 Build') {
            steps {
                echo '🔨 Gradle 빌드 중...'
                sh '''
                    cd ${WORKSPACE_PATH}
                    ./gradlew clean build -x test --no-daemon
                '''
                echo '✅ 빌드 완료'
                echo '📦 JAR 파일: build/libs/inforsion-server-0.0.1-SNAPSHOT.jar'
            }
        }

        stage('🐳 Docker Build') {
            steps {
                echo '🐳 Docker 이미지 빌드 중...'
                script {
                    sh """
                        cd ${WORKSPACE_PATH}
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest
                    """
                }
                echo '✅ Docker 이미지 빌드 완료'
                sh "docker images | grep ${DOCKER_IMAGE} || true"
            }
        }

        stage('📤 Docker Push') {
            steps {
                echo '📤 Docker Hub에 푸시 중...'
                script {
                    sh """
                        echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin
                        docker push ${DOCKER_IMAGE}:${DOCKER_TAG}
                        docker push ${DOCKER_IMAGE}:latest
                        docker logout
                    """
                }
                echo '✅ Docker Hub 푸시 완료'
                echo "이미지: ${DOCKER_IMAGE}:${DOCKER_TAG}"
                echo "이미지: ${DOCKER_IMAGE}:latest"
            }
        }

        // ============================================
        // 선택사항: 자동 배포 (Phase 3)
        // ============================================
        // 사용하려면 주석 해제하고 설정 수정
        //
        // stage('🚀 Deploy') {
        //     when {
        //         branch 'main'  // main 브랜치만 배포
        //     }
        //     steps {
        //         echo '🚀 서버에 배포 중...'
        //         script {
        //             // SSH로 서버 접속 및 배포
        //             sh '''
        //                 ssh -i ~/.ssh/id_rsa user@your-server '
        //                     cd /app
        //                     docker-compose pull app
        //                     docker-compose up -d app
        //                     docker ps
        //                 '
        //             '''
        //         }
        //         echo '✅ 배포 완료'
        //     }
        // }

        // ============================================
        // 선택사항: 헬스 체크
        // ============================================
        // stage('🏥 Health Check') {
        //     steps {
        //         echo '🏥 헬스 체크 중...'
        //         script {
        //             sh '''
        //                 sleep 30
        //                 for i in {1..10}; do
        //                     if curl -f http://your-server:8080/actuator/health; then
        //                         echo "✅ 헬스 체크 성공!"
        //                         exit 0
        //                     fi
        //                     echo "재시도 중... ($i/10)"
        //                     sleep 5
        //                 done
        //                 echo "❌ 헬스 체크 실패!"
        //                 exit 1
        //             '''
        //         }
        //     }
        // }

        stage('🧹 Cleanup') {
            steps {
                echo '🧹 사용하지 않는 이미지 정리 중...'
                sh 'docker image prune -f || true'
                echo '✅ 정리 완료'
            }
        }
    }

    post {
        success {
            echo ''
            echo '✅ =========================================='
            echo '✅  파이프라인 성공!'
            echo '✅ =========================================='
            echo ''
            echo "빌드 번호: ${BUILD_NUMBER}"
            echo "브랜치: ${GIT_BRANCH}"
            echo "Docker 이미지: ${DOCKER_IMAGE}:${DOCKER_TAG}"
            echo ''
            echo '📍 Docker Hub에서 확인:'
            echo "   https://hub.docker.com/r/${DOCKER_IMAGE}"
            echo ''

            // TODO: Slack 알림 추가
            // slackSend(
            //     color: 'good',
            //     message: "✅ 빌드 성공: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
            // )
        }

        failure {
            echo ''
            echo '❌ =========================================='
            echo '❌  파이프라인 실패!'
            echo '❌ =========================================='
            echo ''
            echo "빌드 번호: ${BUILD_NUMBER}"
            echo "브랜치: ${GIT_BRANCH}"
            echo ''
            echo '확인사항:'
            echo '  - 테스트가 통과했는가?'
            echo '  - Docker Hub Credentials가 올바른가?'
            echo '  - 빌드 로그를 확인하세요.'
            echo ''

            // TODO: Slack 알림 추가
            // slackSend(
            //     color: 'danger',
            //     message: "❌ 빌드 실패: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
            // )
        }

        always {
            echo ''
            echo '📊 빌드 정보:'
            echo "  Job: ${JOB_NAME}"
            echo "  Build: #${BUILD_NUMBER}"
            echo "  Workspace: ${WORKSPACE}"
            echo "  완료 시각: ${new Date().toString()}"
            echo ''
        }
    }
}