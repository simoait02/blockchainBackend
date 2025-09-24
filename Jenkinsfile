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
                        -v /var/run/docker.sock:/var/run/docker.sock
                    '''
                    // Reuse the same workspace instead of creating @2
                    reuseNode true
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
                            reuseNode true
                        }
                    }
                    steps {
						sh 'mvn checkstyle:checkstyle'
                        recordIssues(tools: [checkStyle(pattern: 'target/checkstyle-result.xml')])
                    }
                }

                agent {
					docker {
						image 'hadolint/hadolint:latest'
						args '-v $WORKSPACE:/app -w /app'
					}
				}
				steps {
					sh 'hadolint Dockerfile --no-fail -f json | tee hadolint.json'
					recordIssues(tools: [hadoLint(pattern: 'hadolint.json')])
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
                    reuseNode true
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
                    reuseNode true
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
                            # Simple Docker login and push
                            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                            docker push simo3011w/blockchain_app:latest
                            docker logout
                        '''
                    }
                }
            }
        }

        stage("Merge Dev into Staging") {
			steps {
				withCredentials([usernamePassword(credentialsId: 'my-github',
                                                usernameVariable: 'GIT_USERNAME',
                                                passwordVariable: 'GIT_PASSWORD')]) {
					sh '''
                        git config user.name "Jenkins CI"
                        git config user.email "jenkins@example.com"

                        # Set up authentication
                        git config credential.helper store
                        echo "https://${GIT_USERNAME}:${GIT_PASSWORD}@github.com" > ~/.git-credentials

                        git fetch origin
                        git checkout staging
                        git merge origin/dev --no-ff -m "Automated merge dev into staging"
                        git push origin staging

                        # Clean up credentials
                        rm ~/.git-credentials
                    '''
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