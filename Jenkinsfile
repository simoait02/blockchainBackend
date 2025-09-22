pipeline {
	agent any

    environment {
		MAVEN_IMAGE = 'maven:4.0.0-rc-4-eclipse-temurin-17'
        MAVEN_ARGS = '''
            -v $WORKSPACE:/app
            -v /tmp/maven-cache:/root/.m2
            -w /app
            --user root
        '''
    }

    stages {
		stage("Code Stability Check") {
			agent {
				docker {
					image "${MAVEN_IMAGE}"
                    args "${MAVEN_ARGS}"
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
							image "${MAVEN_IMAGE}"
                            args "${MAVEN_ARGS}"
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
