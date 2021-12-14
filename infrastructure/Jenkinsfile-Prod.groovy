pipeline {
    agent any

    environment {
        DOCKER_PROJECT_NAME = "odh-datacollector-flightdata"
        DOCKER_IMAGE = '755952719952.dkr.ecr.eu-west-1.amazonaws.com/odh-datacollector-flightdata'
        DOCKER_TAG = "prod-$BUILD_NUMBER"

        SERVER_PORT = "1000"

        POSTGRES_URL = "jdbc:postgresql://postgres-prod.co90ybcr8iim.eu-west-1.rds.amazonaws.com:5432/project"
        POSTGRES_USER = credentials('odh-datacollector-flightdata-prod-postgres-username')
        POSTGRES_PASSWORD = credentials('odh-datacollector-flightdata-prod-postgres-password')
        
        MQTT_URL = "tcp://odh-mosquitto:1883"
        MQTT_USER = credentials('odh-datacollector-flightdata-test-mqtt-username')
        MQTT_PASSWORD = credentials('odh-datacollector-flightdata-test-mqtt-username')
    }

    stages {
        stage('Configure') {
            steps {
                sh """
                    rm -f .env
                    cp .env.example .env
                    echo 'COMPOSE_PROJECT_NAME=${DOCKER_PROJECT_NAME}' >> .env
                    echo 'DOCKER_IMAGE=${DOCKER_IMAGE}' >> .env
                    echo 'DOCKER_TAG=${DOCKER_TAG}' >> .env

                    echo 'SERVER_PORT=${SERVER_PORT}' >> .env

                    echo 'POSTGRES_URL=${POSTGRES_URL}' >> .env
                    echo 'POSTGRES_USER=${POSTGRES_USER}' >> .env
                    echo 'POSTGRES_PASSWORD=${POSTGRES_PASSWORD}' >> .env

                    echo 'MQTT_URL=${MQTT_URL}' >> .env
                    echo 'MQTT_USER=${MQTT_USER}' >> .env
                    echo 'MQTT_PASSWORD=${MQTT_PASSWORD}' >> .env
                """
            }
        }

        stage('Test') {
            steps {
                sh '''
                    docker-compose --no-ansi build --pull --build-arg JENKINS_USER_ID=$(id -u jenkins) --build-arg JENKINS_GROUP_ID=$(id -g jenkins)
                    docker-compose --no-ansi run --rm --no-deps -u $(id -u jenkins):$(id -g jenkins) datacollector ./mvnw -B -U clean test
                '''
            }
        }
        stage('Build') {
            steps {
                sh '''
                    aws ecr get-login --region eu-west-1 --no-include-email | bash
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml build --pull
                    docker-compose --no-ansi -f infrastructure/docker-compose.build.yml push
                '''
            }
        }
        stage('Deploy') {
            steps {
               sshagent(['jenkins-ssh-key']) {
                    sh """
                        (cd infrastructure/ansible && ansible-galaxy install -f -r requirements.yml)
                        (cd infrastructure/ansible && ansible-playbook --limit=prod deploy.yml --extra-vars "release_name=${BUILD_NUMBER}")
                    """
                }
            }
        }
    }
}
