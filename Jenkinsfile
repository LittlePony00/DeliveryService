pipeline {
    agent {
        docker {
            image 'docker:27.1-dind'
            args '-v /var/run/docker.sock:/var/run/docker.sock --privileged'
        }
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }

        stage('Setup') {
            steps {
                echo 'Setting up permissions...'
                sh 'chmod +x gradlew gradlew.bat'
            }
        }

        stage('Build Docker Images') {
            steps {
                echo 'Building all Docker images...'
                sh '''
                    # Проверяем доступность docker
                    docker --version
                    # Используем docker compose (встроено) или docker-compose
                    if docker compose version &> /dev/null; then
                        docker compose build --progress=plain
                    elif command -v docker-compose &> /dev/null; then
                        docker-compose build --progress=plain
                    else
                        echo "ERROR: docker-compose not found!"
                        exit 1
                    fi
                '''
            }
        }

        stage('Deploy Services') {
            steps {
                echo 'Starting all services...'
                sh '''
                    if docker compose version &> /dev/null; then
                        docker compose up -d
                    elif command -v docker-compose &> /dev/null; then
                        docker-compose up -d
                    else
                        echo "ERROR: docker-compose not found!"
                        exit 1
                    fi
                '''
            }
        }

        stage('Health Check') {
            steps {
                echo 'Waiting for services to start...'
                sh 'sleep 30'
                sh '''
                    echo "Checking services status..."
                    if docker compose version &> /dev/null; then
                        docker compose ps
                    elif command -v docker-compose &> /dev/null; then
                        docker-compose ps
                    fi
                    echo "Checking main service health..."
                    curl -f http://localhost:25566/actuator/health || exit 1
                '''
            }
        }
    }

    post {
        success {
            echo 'Build and deployment completed successfully!'
            echo 'Services are running. Check status with: docker-compose ps'
        }
        failure {
            echo 'Build or deployment failed!'
            echo 'Check logs with: docker-compose logs --tail=50'
        }
        always {
            echo 'Pipeline completed.'
        }
    }
}
