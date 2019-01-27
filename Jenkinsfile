pipeline {
    agent none

	options {
		buildDiscarder(logRotator(numToKeepStr: '5'))
		disableConcurrentBuilds()
	}

	triggers {
		pollSCM('H/5 * * * *')
		upstream (upstreamProjects: 'docker-hyperion-jetty,hyperion/experimental', threshold: hudson.model.Result.SUCCESS)
	}

	stages {
		stage('Build') {
		   agent {
               docker {
                   image 'maven:3.5-jdk-8'
                   label 'docker'
               }
           }


			steps {
			    sh 'git submodule update --init --recursive'
			    sh 'echo `git log -n 1 --pretty=format:"%H"` > '+ env.WORKSPACE +'/src/main/java/com/jeroensteenbeeke/topiroll/beholder/revision.txt'
			    sh 'mvn clean package -U'
			    stash name: 'beholder-war', includes: '**/*.war'
			}
		}
		stage('Dockerize & Publish') {
			agent {
				label 'docker'
			}

			steps {
				sh 'docker pull registry.jeroensteenbeeke.nl/hyperion-jetty:latest'
				unstash 'beholder-war'
				sh 'docker build -t registry.jeroensteenbeeke.nl/beholder:latest .'
				sh 'docker push registry.jeroensteenbeeke.nl/beholder:latest'

			} 
		}
	}
}
