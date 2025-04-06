pipeline {
    agent any
    
    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=/home/kyrellosibrahim/.m2/repository"
        TEST_PASSWORD = "correctPassword" // Make sure this matches your passwords.txt
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                sh 'whoami'
                sh 'mvn test -Dmaven.repo.local=/home/kyrellosibrahim/.m2/repository'
            }
            post {
                failure {
                    error "Unit tests failed. Pipeline execution terminated."
                }
            }
        }
        
        stage('Build JAR') {
            steps {
                sh 'mvn package -DskipTests -Dmaven.repo.local=/home/kyrellosibrahim/.m2/repository'
            }
        }
        
        stage('Build Docker Image') {
            steps {
                sh 'docker build -t xperience-server:latest .'
            }
        }
        
        stage('Deploy') {
            steps {
                sh 'docker stop xperience-server || true'
                sh 'docker rm xperience-server || true'
                sh 'docker run -d -p 8000:8000 --name xperience-server xperience-server:latest'
            }
        }
        
        stage('Validate Deployment') {
            steps {
                script {
                    // Get container IP address
                    def containerIp = sh(
                        script: 'docker inspect -f \'{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}\' xperience-server',
                        returnStdout: true
                    ).trim()
                    
                    // Wait for server to start (with retries)
                    def maxAttempts = 5
                    def attempt = 0
                    def serverReady = false
                    
                    while (attempt < maxAttempts && !serverReady) {
                        attempt++
                        sleep time: 5, unit: 'SECONDS'
                        
                        try {
                            def response = sh(
                                script: "curl -s -X POST http://${containerIp}:8000 -d \"TestEvent#2024-01-01#12:00#TestDescription#${TEST_PASSWORD}#\"",
                                returnStdout: true
                            ).trim()
                            
                            if (response.startsWith("Aksept#")) {
                                serverReady = true
                                echo "Server validation successful: ${response}"
                            } else {
                                echo "Unexpected response (attempt ${attempt}): ${response}"
                            }
                        } catch (Exception e) {
                            echo "Connection attempt ${attempt} failed: ${e.getMessage()}"
                        }
                    }
                    
                    if (!serverReady) {
                        error "Server validation failed after ${maxAttempts} attempts"
                    }
                    
                    // Additional test for rejection case
                    def rejectResponse = sh(
                        script: "curl -s -X POST http://${containerIp}:8000 -d \"TestEvent#invalid-date#12:00#TestDescription#${TEST_PASSWORD}#\"",
                        returnStdout: true
                    ).trim()
                    
                    if (!rejectResponse.startsWith("Reject#")) {
                        error "Rejection test failed. Expected 'Reject#' but got: ${rejectResponse}"
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
            // Clean up container on failure
            sh 'docker stop xperience-server || true'
            sh 'docker rm xperience-server || true'
        }
    }
}
