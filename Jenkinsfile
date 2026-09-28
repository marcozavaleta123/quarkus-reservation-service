pipeline {
    agent any
    tools {
        maven 'maven3.9.16'
    }
    triggers {
        // cron('* * * * *')
        githubPush()
    }
    stages {
        stage('Compile') {
            steps {
                sh 'mvn clean compile -B -ntp'
            }
        }
        stage('Test') {
            steps {
               sh 'mvn test -B -ntp'
               sh 'echo "=== Surefire reports ==="'
               sh 'find target -type f -name "*.xml" -print || true'
            }
        post {
            always {
                junit testResults: 'target/surefire-reports/*.xml',
                  allowEmptyResults: true
            }
        }
        }
        stage('Coverage') {
           steps {
            sh 'mvn jacoco:report -B -ntp'
            sh 'echo "=== JaCoCo report ==="'
            sh 'find target/site/jacoco -type f -print || true'
           }
           post {
            success {
                recordCoverage(
                    tools: [[
                        parser: 'JACOCO',
                        pattern: 'target/site/jacoco/jacoco.xml'
                    ]]
                )
            }
           }
       }       
        stage('Package') {
            steps {
                sh 'mvn package -DskipTests -B -ntp'
            }
        }
        /**stage('SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube'){//nombre del sonarqube configurado en el entorno system de jenkins
                     sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -B -ntp -Dsonar.branch.name=master -Dsonar.branch.target=main"
                     //sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -B -ntp"
                }
            }
        }**/
        stage('SonarQube') {
            steps {
                withSonarQubeEnv('sonarqube'){//nombre del sonarqube configurado en el entorno system de jenkins
                    sh 'env | sort'
                    sh '''
                        echo "===== VARIABLES JENKINS ====="
                        echo "CHANGE_ID=${CHANGE_ID}"
                        echo "CHANGE_BRANCH=${CHANGE_BRANCH}"
                        echo "CHANGE_TARGET=${CHANGE_TARGET}"
                        echo "GIT_BRANCH=${GIT_BRANCH}"
                        echo "BRANCH_NAME=${BRANCH_NAME}"
                        echo "JOB_NAME=${JOB_NAME}"
                        echo "BUILD_NUMBER=${BUILD_NUMBER}"
                        echo "BUILD_URL=${BUILD_URL}"
                        echo "WORKSPACE=${WORKSPACE}"
                        echo "================================"
                    '''
                    script {
                        if (env.CHANGE_ID) {
                            sh """
                                mvn sonar:sonar -B -ntp \
                                -Dsonar.pullrequest.key=${env.CHANGE_ID} \
                                -Dsonar.pullrequest.branch=${env.CHANGE_BRANCH} \
                                -Dsonar.pullrequest.base=${env.CHANGE_TARGET}
                            """
                        } else {
                            def branchName = GIT_BRANCH.replaceFirst('^origin/', '')
                            println "Branch name: ${branchName}"
                            sh "mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -B -ntp -Dsonar.branch.name=${branchName} -Dsonar.branch.target=${branchName}"
                        }
                    }
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: true
        }
        cleanup {
            cleanWs()
        }
    }
}