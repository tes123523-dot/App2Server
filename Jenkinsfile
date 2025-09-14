pipeline {
    agent any

    tools {
        jdk 'Java21'      // Your JDK configured in Jenkins
        maven 'Maven3'    // Your Maven configured in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat "mvn clean install -DskipTests"
            }
        }

        stage('Test') {
            steps {
                bat "mvn test"
            }
        }

        stage('Package') {
            steps {
                bat "mvn package"
            }
        }

        stage('Archive Artifacts') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar, **/target/*.war', fingerprint: true
            }
        }
    }

    post {
        success {
            echo "✅ Build SUCCESS"
        }
        failure {
            echo "❌ Build FAILED"
        }
    }
}
