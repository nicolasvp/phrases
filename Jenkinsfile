pipeline {
    agent any

    tools {
        maven "3.6.3"
    }

    stages {
        stage("Build") {
            steps {
                sh "mvn -version"
                sh "mvn clean compile"
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}