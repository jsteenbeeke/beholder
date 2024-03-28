@Library(value = 'jenkins-shared-library@main', changelog = false)
import com.jeroensteenbeeke.hyperion.*

String application_hash = null
def hyperion = new Hyperion(this)
Variant variant = Variant.JETTY_JAKARTA

def upstreams = hyperion.determineUpstreamProjects(variant)
def pollInterval = hyperion.scmPollInterval()

pipeline {
    agent none

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        disableConcurrentBuilds()
    }

    triggers {
        pollSCM(pollInterval)
        upstream(upstreamProjects: upstreams, threshold: hudson.model.Result.SUCCESS)
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

                sh 'mvn clean package -U -B -ntp'
                stash name: 'beholder-war', includes: '**/*.war'
            }
        }
        stage('Dockerize & Publish') {
            agent {
                label 'docker'
            }

            steps {
                script {
                    hyperion.pullParentImage(variant)
                    hyperion.replaceDockerFileParentImage(variant, 'Dockerfile')
                }
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
