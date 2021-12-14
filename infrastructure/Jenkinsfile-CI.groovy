pipeline {
    agent {
        dockerfile {
            filename 'infrastructure/docker/Dockerfile'
            additionalBuildArgs '--target dev --build-arg JENKINS_USER_ID=`id -u jenkins` --build-arg JENKINS_GROUP_ID=`id -g jenkins`'
        }
    }

    stages {
        stage('Test') {
            steps {
                sh './mvnw -B -U clean test'
            }
        }
    }
}
