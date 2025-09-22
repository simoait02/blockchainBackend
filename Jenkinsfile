pipeline {
	agent any
    stages {
		stage("code stability check") {
			agent {
				docker {
					image 'maven:4.0.0-rc-4-eclipse-temurin-17'
					args '''
						-v $WORKSPACE:/app
						-v /tmp/maven-cache:/root/.m2
						-w /app
						--user root
					'''
				}
          	}
          steps {
				sh 'mvn clean package'
          }
       }

       stage("code quality") {
			agent {
				docker {
					image 'maven:4.0.0-rc-4-eclipse-temurin-17'
					args '''
						-v $WORKSPACE:/app
						-v /tmp/maven-cache:/root/.m2
						-w /app
						--user root
					'''
				}
			}
			steps{
				sh 'mvn checkstyle:checkstyle'
			}
		}
    }
}