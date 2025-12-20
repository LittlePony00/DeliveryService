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

        stage('Build Analytics Service') {
            steps {
                dir("${PROJECT_DIR}/analytics-service") {
                    sh '../gradlew build -x test'
                }
            }
        }

        stage('Build Audit Service') {
            steps {
                dir("${PROJECT_DIR}/audit-service") {
                    sh '../gradlew build -x test'
                }
            }
        }

        stage('Build Statistics Service') {
            steps {
                dir("${PROJECT_DIR}/statistics-service") {
                    sh '../gradlew build -x test'
                }
            }
        }

        stage('Build Main Service') {
            steps {
                dir("${PROJECT_DIR}/main") {
                    sh '../gradlew build -x test'
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
