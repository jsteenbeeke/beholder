pipeline {
    agent {
	    label 'docker'
    }

	options {
		buildDiscarder(logRotator(numToKeepStr: '5'))
		disableConcurrentBuilds()
	}

	stages {
		stage('Build') {
		   agent {
               docker {
                   image 'maven:3.5-jdk-8'
                   label 'docker'
                   args '-u root'
               }
           }

			steps {
	            sh 'echo `git log -n 1 --pretty=format:"%H"` > src/main/java/com/jeroensteenbeeke/topiroll/beholder/revision.txt'
			    sh 'mvn clean package'
			}
		}
		stage('Dockerize') {
			steps {
				sh 'docker pull registry.jeroensteenbeeke.nl/hyperion-jetty:latest'
				sh 'docker build -t registry.jeroensteenbeeke.nl/beholder:latest .'
			} 
		}
		stage('Publish') {
			steps {
				sh 'docker push registry.jeroensteenbeeke.nl/beholder:latest'
			}
		}
	}
}
