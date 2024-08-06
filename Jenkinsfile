pipeline {
    agent any
    parameters {
        string(name: 'VERSION', defaultValue: '', description: 'version to deploy')
    }

    stages {
        stage("build") {
            steps {
                echo 'building the application...'
            }
        }
        stage("test") {
            steps {
                echo 'testing the application...'
            }
        }
        stage("deploy") {
            steps {
                echo 'deploying the application...'
            }
        }
    }

    post {
        always {
            // will always be executed whether the jobs failed or not. For example, send email...
        }

        success {
            // will run on failure
        }

        failure {
            // will run on failure
        }
    }
}