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

       stage("Quality Checks") {
			parallel {
				stage("Code Quality") {
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
						sh 'mvn checkstyle:checkstyle'
                        recordIssues(tools: [checkStyle(pattern: 'target/checkstyle-result.xml')])
                    }
                }

                stage("Hadolint") {
					steps {
						sh "hadolint Dockerfile --no-fail -f json | tee -a hadolint.json"
                        recordIssues(tools: [hadoLint(pattern: 'hadolint.json')])
                    }
                }
            }
        }
    }
}
