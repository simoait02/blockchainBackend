pipeline {
	agent any
    stages {
		stage("code stability check") {
			agent {
				docker {
					image 'maven:4.0.0-rc-4-eclipse-temurin-17'
                    args '''
                        -v $WORKSPACE:/app
                        -v $HOME/.m2:/root/.m2
                        -e HOME=/root
                        -w /app
                    '''
                }
            }
            steps {
				sh 'mvn clean package'
            }
        }
    }
}
