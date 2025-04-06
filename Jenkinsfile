pipeline {
    agent any
    
    environment {
        MAVEN_OPTS = "-Dmaven.repo.local=/home/kyrellosibrahim/.m2/repository"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test') {
            steps {
                sh 'whoami'  // Debugging step to check user
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
            // Check container status first
            def status = sh(
                script: 'docker inspect -f "{{.State.Status}}" xperience-server',
                returnStdout: true
            ).trim()
            echo "Container status: ${status}"
            
            // Get server logs
            def logs = sh(
                script: 'docker logs xperience-server',
                returnStdout: true
            ).trim()
            echo "Server logs:\n${logs}"
            
            // Only test if container is running
            if (status == "running") {
                sleep 10
                echo "Testing server response..."
                def response = sh(
                    script: 'curl -v -X POST http://localhost:8000 -d "TestEvent#2024-01-01#12:00#TestDescription#TestPassword#"',
                    returnStdout: true
                ).trim()
                echo "Server response: ${response}"
            } else {
                error "Container is not running!"
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
        }
    }
}

