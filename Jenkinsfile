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
        stage('Git Info') {
            steps {
                echo "========== GIT INFO =========="
                echo "BRANCH_NAME   = ${env.BRANCH_NAME}"
                echo "GIT_BRANCH    = ${env.GIT_BRANCH}"
                echo "CHANGE_ID     = ${env.CHANGE_ID}"
                echo "CHANGE_BRANCH = ${env.CHANGE_BRANCH}"
                echo "CHANGE_TARGET = ${env.CHANGE_TARGET}"
                echo "GIT_COMMIT    = ${env.GIT_COMMIT}"
                echo "=============================="
            }
        }
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
                    echo "===== VARIABLES JENKINS ====="
                    echo "CHANGE_ID     = ${env.CHANGE_ID ?: 'N/A'}"
                    echo "CHANGE_BRANCH = ${env.CHANGE_BRANCH ?: 'N/A'}"
                    echo "CHANGE_TARGET = ${env.CHANGE_TARGET ?: 'N/A'}"
                    echo "GIT_BRANCH    = ${env.GIT_BRANCH ?: 'N/A'}"
                    echo "BRANCH_NAME   = ${env.BRANCH_NAME ?: 'N/A'}"
                    echo "JOB_NAME      = ${env.JOB_NAME ?: 'N/A'}"
                    echo "BUILD_NUMBER  = ${env.BUILD_NUMBER ?: 'N/A'}"
                    echo "BUILD_URL     = ${env.BUILD_URL ?: 'N/A'}"
                    echo "WORKSPACE     = ${env.WORKSPACE ?: 'N/A'}"
                    echo "================================"
                    
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