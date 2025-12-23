pipeline {
    agent any

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
                echo 'Setting up environment...'
                sh '''
                    # Установка Docker CLI если не установлен
                    if ! command -v docker &> /dev/null; then
                        echo "Installing Docker CLI..."
                        apt-get update -qq
                        apt-get install -y -qq docker.io || apt-get install -y -qq docker-ce-cli
                    fi
                    docker --version
                    
                    # Установка docker-compose если не установлен
                    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
                        echo "Installing docker-compose..."
                        curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
                        chmod +x /usr/local/bin/docker-compose
                    fi
                    
                    # Проверка доступности
                    if docker compose version &> /dev/null; then
                        echo "Using: docker compose"
                    elif command -v docker-compose &> /dev/null; then
                        echo "Using: docker-compose"
                        docker-compose --version
                    else
                        echo "ERROR: docker-compose not available!"
                        exit 1
                    fi
                    
                    chmod +x gradlew gradlew.bat
                '''
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
