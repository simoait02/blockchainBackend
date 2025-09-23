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
                // Verify the image was built
                sh 'docker images | grep simo3011w/blockchain_app'
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
							try {
								sh '''
									set -e  # Exit on any error

									echo "=== Docker Push Debug Information ==="
									echo "Current user: $(whoami)"
									echo "Docker version: $(docker --version)"
									echo "Available images:"
									docker images | grep -E "(REPOSITORY|simo3011w/blockchain_app)"

									# Create a temporary docker config directory
									export DOCKER_CONFIG_DIR="/tmp/docker-config-$$"
									echo "Creating Docker config directory: $DOCKER_CONFIG_DIR"
									mkdir -p "$DOCKER_CONFIG_DIR"

									# Create docker config that disables credential storage
									cat > "$DOCKER_CONFIG_DIR/config.json" << 'EOF'
{
	"auths": {},
	"credsStore": "",
	"credHelpers": {}
}
EOF

									echo "Docker config created:"
									cat "$DOCKER_CONFIG_DIR/config.json"

									# Set DOCKER_CONFIG environment variable
									export DOCKER_CONFIG="$DOCKER_CONFIG_DIR"
									echo "DOCKER_CONFIG set to: $DOCKER_CONFIG"

									# Login to Docker Hub
									echo "=== Logging in to Docker Hub ==="
									echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

									# Verify login worked
									echo "=== Verifying login ==="
									if docker info | grep -q "Username: $DOCKER_USERNAME"; then
										echo "✓ Successfully logged in as $DOCKER_USERNAME"
									else
										echo "✗ Login verification failed"
										docker info | grep -A 5 -B 5 "Registry"
									fi

									# Check if image exists before pushing
									echo "=== Checking if image exists locally ==="
									if docker inspect simo3011w/blockchain_app:latest >/dev/null 2>&1; then
										echo "✓ Image simo3011w/blockchain_app:latest found locally"
										docker inspect simo3011w/blockchain_app:latest --format='{{.Id}} {{.Created}}'
									else
										echo "✗ Image simo3011w/blockchain_app:latest not found locally"
										echo "Available images:"
										docker images
										exit 1
									fi

									# Push the image with verbose output
									echo "=== Pushing image to Docker Hub ==="
									docker push simo3011w/blockchain_app:latest

									# Verify the push worked
									echo "=== Verifying push completed ==="
									echo "✓ Push completed successfully"

									# Logout
									echo "=== Logging out ==="
									docker logout

									# Cleanup
									rm -rf "$DOCKER_CONFIG_DIR"
									echo "✓ Cleanup completed"

									echo "=== Image pushed and logged out successfully! ==="
								'''
							} catch (Exception e) {
								echo "❌ Docker push failed with error: ${e.getMessage()}"
								sh '''
									echo "=== Debug Information After Failure ==="
									docker images | grep -E "(REPOSITORY|simo3011w)" || echo "No simo3011w images found"
									docker info | grep -A 10 -B 5 "Registry" || echo "No registry info found"
									echo "Docker daemon status:"
									docker version || echo "Docker version failed"
								'''
								throw e
							}
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