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
                echo 'Building all services using Docker with Java 17...'
                script {
                    def workspace = env.WORKSPACE
                    sh """
                        docker run --rm \
                            -v "${workspace}":/workspace \
                            -w /workspace \
                            gradle:8.13-jdk17 \
                            bash -c "chmod +x ./gradlew && ./gradlew :analytics-service:build -x test :audit-service:build -x test :statistics-service:build -x test :main:build -x test"
                    """
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
