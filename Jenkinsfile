pipeline {
	agent any

    environment {
		DOCKER_IMAGE = 'simo3011w/blockchain_app:latest'
        MAVEN_CACHE = '/tmp/maven-cache'
    }

    stages {
		stage("Prepare Environment") {
			steps {
				script {
					// Clean any existing lock files and prepare directories
                    sh '''
                        # Clean lock files
                        find $WORKSPACE -name "*.lock" -type f -delete 2>/dev/null || true
                        find $WORKSPACE -name "config.lock" -type f -delete 2>/dev/null || true

                        # Ensure maven cache directory exists
                        mkdir -p ${MAVEN_CACHE}

                        # Fix permissions
                        chmod -R 755 $WORKSPACE 2>/dev/null || true
                    '''
                }
            }
        }

        stage("Code Stability Check") {
			steps {
				script {
					sh '''
                        echo "Running Maven clean package..."
                        docker run --rm \
                            -v "$WORKSPACE":/app \
                            -v "${MAVEN_CACHE}":/root/.m2 \
                            -w /app \
                            --user root \
                            maven:4.0.0-rc-4-eclipse-temurin-17 \
                            mvn clean package -DskipTests
                    '''
                }
            }
        }

        stage("Quality Checks") {
			parallel {
				stage("Code Quality") {
					steps {
						script {
							sh '''
                                echo "Running Checkstyle analysis..."
                                docker run --rm \
                                    -v "$WORKSPACE":/app \
                                    -v "${MAVEN_CACHE}":/root/.m2 \
                                    -w /app \
                                    --user root \
                                    maven:4.0.0-rc-4-eclipse-temurin-17 \
                                    mvn checkstyle:checkstyle
                            '''
                        }
                        recordIssues(
                            enabledForFailure: true,
                            tools: [checkStyle(pattern: 'target/checkstyle-result.xml')]
                        )
                    }
                }

                pipeline {
					agent any

					stages {
						stage('Hadolint') {
							steps {
								script {
													// Ensure Dockerfile exists
									if (!fileExists('Dockerfile')) {
														writeFile file: 'hadolint.json', text: '[]'
										echo "Dockerfile not found, created empty hadolint.json"
									} else {
										sh '''
											docker run --rm -i \
												-v "$WORKSPACE":/workspace \
												-w /workspace \
												hadolint/hadolint:latest \
												hadolint Dockerfile --no-fail -f json > hadolint.json || echo "[]" > hadolint.json
										'''
									}
								}
								recordIssues(
									enabledForFailure: true,
									tools: [hadoLint(pattern: 'hadolint.json')]
								)
							}
						}
					}
				}

            }
        }

        stage("Code Coverage") {
			steps {
				script {
					sh '''
                        echo "Running tests and generating coverage..."
                        docker run --rm \
                            -v "$WORKSPACE":/app \
                            -v "${MAVEN_CACHE}":/root/.m2 \
                            -w /app \
                            --user root \
                            maven:4.0.0-rc-4-eclipse-temurin-17 \
                            mvn clean test
                    '''
                }
                recordIssues(
                    enabledForFailure: true,
                    tools: [junitParser(pattern: 'target/surefire-reports/*.xml')]
                )
            }
        }

        stage("OWASP Dependency Check") {
			steps {
				script {
					sh '''
                        echo "Running OWASP Dependency Check..."
                        docker run --rm \
                            -v "$WORKSPACE":/app \
                            -v "${MAVEN_CACHE}":/root/.m2 \
                            -w /app \
                            --user root \
                            maven:4.0.0-rc-4-eclipse-temurin-17 \
                            mvn org.owasp:dependency-check-maven:check
                    '''
                }
                publishHTML([
                    allowMissing: true,
                    alwaysLinkToLastBuild: false,
                    keepAll: false,
                    reportDir: 'target',
                    reportFiles: 'dependency-check-report.html',
                    reportName: 'OWASP Dependency Check Report',
                    reportTitles: ''
                ])
            }
        }

        stage("Build Docker Image") {
			steps {
				script {
					sh '''
                        echo "Building Docker image..."
                        docker build -t ${DOCKER_IMAGE} .
                        echo "Docker image built successfully: ${DOCKER_IMAGE}"
                    '''
                }
            }
        }

        stage("Snyk Security Scan") {
			steps {
				script {
					try {
						echo "Running Snyk security scan..."
                        snykSecurity(
                            snykInstallation: 'snyk',
                            snykTokenId: 'snyk-token',
                            additionalArguments: "--docker ${env.DOCKER_IMAGE} --file=Dockerfile",
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
			steps {
				script {
					withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials',
                                                    usernameVariable: 'DOCKER_USERNAME',
                                                    passwordVariable: 'DOCKER_PASSWORD')]) {
						sh '''
                            echo "Logging into Docker Hub..."
                            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

                            echo "Pushing Docker image..."
                            docker push ${DOCKER_IMAGE}

                            echo "Logging out..."
                            docker logout

                            echo "Image pushed successfully: ${DOCKER_IMAGE}"
                        '''
                    }
                }
            }
        }

        stage("Merge Dev into Staging") {
			steps {
				sh '''
					echo 'Configuring Git...'
					git config user.name "Jenkins CI"
					git config user.email "jenkins@example.com"
					git config credential.helper 'store --file=.git-credentials'

					echo 'Fetching latest changes...'
					git fetch origin

					echo 'Checking out staging branch...'
					git fetch origin staging:staging
					git checkout staging

					echo 'Merging dev into staging...'
					git merge origin/dev --no-ff -m "Automated merge dev into staging"

					echo 'Pushing updated staging branch...'
					git push origin staging
				'''
			}
		}
    }

    post {
		always {
			script {
				// Clean up Docker images to save space
                sh '''
                    # Remove dangling images
                    docker image prune -f || true
                '''
            }
            cleanWs()
        }
        success {
			echo "✅ Pipeline completed successfully!"
        }
        failure {
			echo "❌ Pipeline failed. Check the logs for details."
        }
        unstable {
			echo "⚠️ Pipeline completed with warnings."
        }
    }
}