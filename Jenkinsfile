pipeline {
	agent any

    stages {
		stage("Code Stability Check") {
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
					agent any // Runs on local Jenkins agent
                    steps {
						sh "hadolint Dockerfile --no-fail -f json | tee -a hadolint.json"
                        recordIssues(tools: [hadoLint(pattern: 'hadolint.json')])
                    }
                }
            }
        }

        stage("Code Coverage") {
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
				sh 'mvn clean test'
                recordIssues(tools: [junitParser(pattern: 'target/surefire-reports/*.xml')])
            }
        }

        stage("OWASP Dependency Check") {
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
				sh 'mvn org.owasp:dependency-check-maven:check'
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: false,
                    reportDir: 'target',
                    reportFiles: 'dependency-check-report.html',
                    reportName: 'Dependency Check Report',
                    reportTitles: ''
                ])
            }
        }

        stage("Build Docker Image") {
			agent any
            steps {
				sh 'docker build -t simo3011w/blockchain_app:latest .'
            }
        }
        stage ("Snyk Security Scan"){
			agent any
			steps {
				script {
					try {
						snykSecurity(
							snykInstallation: 'snyk',
							snykTokenId: 'snyk-token',
							additionalArguments: '--docker simo3011w/blockchain_app:latest --file=Dockerfile',
							failOnError: false,
							failOnIssues: false,
							monitorProjectOnBuild: false
						)
					} catch (Exception e) {
						echo "Snyk scan encountered an issue: ${e.getMessage()}"
						echo "Continuing pipeline execution..."
						currentBuild.result = 'UNSTABLE'
					}
				}
			}
		}
		stage("Push Image to Registry") {
			agent any
			steps {
						script {
							withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials',
													usernameVariable: 'DOCKER_USERNAME',
													passwordVariable: 'DOCKER_PASSWORD')]) {
								sh '''
							# Initialize pass with your GPG key
							pass init 51905CE936D9D4AC388E305B7C8DE2A8341FE095

							# Create Docker config to use pass
							mkdir -p ~/.docker
							echo '{"credsStore":"pass"}' > ~/.docker/config.json

							# Login using credentials from Jenkins
							docker login -u "$DOCKER_USERNAME" -p "$DOCKER_PASSWORD"

							# Push the image
							docker push simo3011w/blockchain_app:latest

							# Logout for security
							docker logout
						'''
					}
				}
			}
		}
    }

    post {
		always {
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
