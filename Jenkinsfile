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
					agent any  // Runs on local Jenkins agent
                    steps {
						sh "hadolint Dockerfile --no-fail -f json | tee -a hadolint.json"
                        recordIssues(tools: [hadoLint(pattern: 'hadolint.json')])
                    }
                }
            }
        }
        stage("code coverage"){
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
				sh 'mvn clean test'
				recordIssues(tools: [junitParser(pattern: 'target/surefire-reports/*.xml')])
			}
		}

		stage ("owasp dependency check") {
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
				sh 'mvn org.owasp:dependency-check-maven:check'
				publishHTML([allowMissing: false, alwaysLinkToLastBuild:false, keepAll: false, reportDir: 'target', reportFiles:'dependency-check-report.html', reportName: 'Dependency Check Report', reportTitles: ''])
			}
		}

		stage("build docker image") {
			agent any
			steps {
				sh 'docker build -t simo3011w/blockchain_app:latest .
			}
		}

    }

    post {
		always {
			// Clean up workspace
            cleanWs()
        }
        success {
			echo "Pipeline completed successfully!"
        }
        failure {
			echo "Pipeline failed. Check the logs for details."
        }
    }
}