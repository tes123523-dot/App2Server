pipeline {
    agent any

    tools {
        jdk 'Java21'      // The JDK name you configured in Jenkins
        maven 'Maven3'    // The Maven name you configured in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                // Fetch source code
                checkout scm
            }
        }

        stage('Build') {
            steps {
                // Clean & build project
                sh "mvn clean install -DskipTests"
            }
        }

        stage('Test') {
            steps {
                // Run unit + integration tests
                sh "mvn test"
            }
        }

        stage('Package') {
            steps {
                // Package application (JAR/WAR)
                sh "mvn package"
            }
        }

        stage('Archive Artifacts') {
            steps {
                // Save final build artifact in Jenkins
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
