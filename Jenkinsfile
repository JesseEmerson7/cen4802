pipeline {
    agent any

    stages {
        stage('Build, test, and package') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew clean build'
                    } else {
                        bat 'gradlew.bat clean build'
                    }
                }
            }
        }
    }

    post {
        always {
            junit 'build/test-results/test/*.xml'
        }
        success {
            archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
        }
    }
}
