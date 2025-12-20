pipeline {
    agent any

    environment {
        PROJECT_DIR = "${WORKSPACE}/MusicSpring"
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }

        stage('Build All Services') {
            steps {
                dir("${PROJECT_DIR}") {
                    sh './gradlew :analytics-service:build -x test'
                    sh './gradlew :audit-service:build -x test'
                    sh './gradlew :statistics-service:build -x test'
                    sh './gradlew :main:build -x test'
                }
            }
        }
    }

    post {
        success {
            echo 'Build completed successfully!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
