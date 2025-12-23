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
                sh 'docker-compose build --progress=plain'
            }
        }

        stage('Deploy Services') {
            steps {
                echo 'Starting all services...'
                sh 'docker-compose up -d'
            }
        }

        stage('Health Check') {
            steps {
                echo 'Waiting for services to start...'
                sh 'sleep 30'
                sh '''
                    echo "Checking services status..."
                    docker-compose ps
                    echo "Checking main service health..."
                    curl -f http://localhost:25566/actuator/health || exit 1
                '''
            }
        }
    }

    post {
        success {
            echo 'Build and deployment completed successfully!'
            echo 'Services are running:'
            sh 'docker-compose ps'
        }
        failure {
            echo 'Build or deployment failed!'
            sh 'docker-compose logs --tail=50'
        }
        always {
            echo 'Cleaning up...'
        }
    }
}
