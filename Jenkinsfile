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
                    # Create GPG key configuration file
                    echo "%echo Generating GPG key for Jenkins Docker" > /tmp/gpg-key-config
                    echo "Key-Type: RSA" >> /tmp/gpg-key-config
                    echo "Key-Length: 2048" >> /tmp/gpg-key-config
                    echo "Subkey-Type: RSA" >> /tmp/gpg-key-config
                    echo "Subkey-Length: 2048" >> /tmp/gpg-key-config
                    echo "Name-Real: Jenkins Docker" >> /tmp/gpg-key-config
                    echo "Name-Email: jenkins@docker.local" >> /tmp/gpg-key-config
                    echo "Expire-Date: 0" >> /tmp/gpg-key-config
                    echo "%no-protection" >> /tmp/gpg-key-config
                    echo "%commit" >> /tmp/gpg-key-config
                    echo "%echo GPG key created" >> /tmp/gpg-key-config

                    # Generate GPG key
                    gpg --batch --generate-key /tmp/gpg-key-config

                    # Get the key ID
                    GPG_KEY_ID=$(gpg --list-secret-keys --keyid-format LONG | grep sec | cut -d'/' -f2 | cut -d' ' -f1)
                    echo "Generated GPG Key ID: $GPG_KEY_ID"

                    # Initialize pass with the new key
                    pass init $GPG_KEY_ID

                    # Create Docker config to use pass
                    mkdir -p ~/.docker
                    echo '{"credsStore":"pass"}' > ~/.docker/config.json

                    # Login using password-stdin
                    echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

                    # Push the image
                    docker push simo3011w/blockchain_app:latest

                    # Logout
                    docker logout

                    # Cleanup
                    rm /tmp/gpg-key-config
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
