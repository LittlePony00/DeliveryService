pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }

        stage('Build All Services') {
            steps {
                echo 'Building all services...'
                sh 'chmod +x ./gradlew'
                sh './gradlew :analytics-service:build -x test :audit-service:build -x test :statistics-service:build -x test :main:build -x test'
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