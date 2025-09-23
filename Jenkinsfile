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
					def userChoice = input(
						message: 'Do you want to push the Docker image to the registry?',
						parameters: [
							choice(choices: ['No', 'Yes'], description: 'Select Yes to push', name: 'PushApproval')
						]
					)

					if (userChoice == 'Yes') {
						echo "Approval received, pushing image..."
						withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials',
														usernameVariable: 'DOCKER_USERNAME',
														passwordVariable: 'DOCKER_PASSWORD')]) {

							sh '''
								# Create a temporary docker config directory
								export DOCKER_CONFIG_DIR="/tmp/docker-config-$$"
								mkdir -p "$DOCKER_CONFIG_DIR"

								# Create docker config that disables credential storage
								cat > "$DOCKER_CONFIG_DIR/config.json" << 'EOF'
		{
			"auths": {},
			"credsStore": "",
			"credHelpers": {}
		}
		EOF

								# Set DOCKER_CONFIG environment variable
								export DOCKER_CONFIG="$DOCKER_CONFIG_DIR"

								# Login to Docker Hub
								echo "Logging in to Docker Hub..."
								echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

								# Push the image
								echo "Pushing image..."
								docker push simo3011w/blockchain_app:latest

								# Logout
								docker logout

								# Cleanup
								rm -rf "$DOCKER_CONFIG_DIR"

								echo "Image pushed and logged out successfully!"
							'''
						}
					} else {
								echo "Push skipped by user."
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
