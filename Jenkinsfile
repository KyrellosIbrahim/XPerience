pipeline {
    agent any

    environment {
        // Sets Maven repo inside Jenkins workspace (accessible to the Jenkins user)
        MAVEN_OPTS = "-Dmaven.repo.local=${env.WORKSPACE}/.m2/repository"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                sh 'whoami'  // Debugging: Verify Jenkins user
                sh 'mvn test'  // Fixed missing quote
            }
            post {
                failure {
                    error "Unit tests failed. Pipeline execution terminated."
                }
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn package -DskipTests'  // Removed redundant repo path (already in MAVEN_OPTS)
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Check if Docker is available (best practice)
                    def dockerCmd = "docker build -t xperience-server:latest ."
                    try {
                        sh dockerCmd
                    } catch (Exception e) {
                        error "Failed to build Docker image. Ensure Jenkins has Docker permissions."
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // Gracefully stop/remove old container (if exists)
                    sh 'docker stop xperience-server || true'
                    sh 'docker rm xperience-server || true'
                    // Run new container
                    sh 'docker run -d -p 8000:8000 --name xperience-server xperience-server:latest'
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline completed (status: ${currentBuild.result ?: 'SUCCESS'})"
            // Optional: Clean up workspace or Docker containers on failure
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline execution failed!'
            // Optional: Notify team via Slack/Email
        }
    }
}
