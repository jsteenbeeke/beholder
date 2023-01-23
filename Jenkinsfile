library 'jenkins-shared-library@main'

String application_hash = null

pipeline {
    agent none

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }

    triggers {
        pollSCM('H/5 * * * *')
        upstream(upstreamProjects: 'docker-hyperion-jetty-jdk19,hyperion/v2.0.x', threshold: hudson.model.Result.SUCCESS)
    }

    stages {
        stage('Build') {
            agent {
                docker {
                    image 'registry.jeroensteenbeeke.nl/maven:latest'
                    label 'docker'
                }
            }

            steps {
                sh 'git submodule update --init --recursive'

                addGitMetadata package: 'com.jeroensteenbeeke.topiroll.beholder'

                sh 'mvn clean package -U'
                stash name: 'beholder-war', includes: '**/*.war'
            }
        }
        stage('Dockerize & Publish') {
            agent {
                label 'docker'
            }

            steps {
                sh 'docker pull registry.jeroensteenbeeke.nl/hyperion-jetty:10-jdk19'
                unstash 'beholder-war'
				script {
					application_hash = dockerizeAndPublish image: 'registry.jeroensteenbeeke.nl/beholder:latest'
				}
            }
        }
        stage('Trigger Application update') {
            steps {
                argoUpdate repository: 'git@bitbucket.org:jsteenbeeke/home-server-infrastructure.git',
                        folder: 'applications/beholder',
                        digests: [ application_hash ]
            }
        }
    }
}
