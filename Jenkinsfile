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
        stage("Testing") {
            steps {
                sh "mvn test -DconfigUrl=http://config-server:1111 -Dspring.profiles.active=test"
            }
        }        
    }

    post {
        always {
            cleanWs()
        }
    }
}