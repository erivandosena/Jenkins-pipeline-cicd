/* Jenkinsfile (Declarative Pipeline) */

pipeline {
    agent any
    environment {
        dockerImage = '${image}'
    }
    stages {
        stage('Removendo container legado') {
            steps {
                script {
                    try {
                        sh 'docker rm -f app-exemplo-mobead-dev'
                    } catch (Exception e) {
                        sh 'echo $e'
                    }
                }
            }
        }
        stage('Subindo container de desenvolvimento') {
            steps {
                script {
                    try {
                        sh 'docker run -d --name app-exemplo-mobead-dev --restart always -p 8082:80 ' + dockerImage
                        } catch (Exception e) {
                            slackSend color: 'error', message: "[FALHA] Não foi possível subir o container em desenvolvimento ${BUILD_URL} em ${currentBuild.duration}s"
                            sh 'echo $e'
                            currentBuild.result = 'ABORTED'
                            error('Erro')
                        }
                }
            }
        }
        stage('Notificando o usuário') {
            steps {
                slackSend color: 'good', message: "[SUCESSO] Novo build de desenvolvimento disponível em: http://138.36.3.60:8082/"
            }
        }
        stage('Encaminhando para produção') {
            steps {
                script {
                    try {
                        //build job: 'Producao-pipeline-freestyle', wait: false, parameters: [string(name: 'image', value: "${image}")]
                        build job: 'Pipeline-producao', wait: false, parameters: [string(name: 'IMAGE', value: "${params.image}")]
                        } catch (Exception e) {
                            slackSend color: 'error', message: "[FALHA] Não foi possível enviar o container para produção ${BUILD_URL} em ${currentBuild.duration}s"
                            sh 'echo $e'
                            currentBuild.result = 'ABORTED'
                            error('Erro')
                        }
                }
            }
        }
        /*
        stage('Fazer o deploy em produção?') {
            steps {
                script {
                    slackSend color: 'warning', message: "Para aplicar a(s) mudança(s) na PRODUÇÃO, acesse o link em 1 hora: http://138.36.3.60:8081/job/${JOB_NAME}/" //${JOB_URL} 
                    timeout(time: 1, unit: 'HOURS') {
                        input(id: 'Deploy Gate', message: 'Deploy na Produção?', ok: 'Deploy')
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    try {
                        build job: 'Pipeline-producao-freestyle', wait: false, parameters: [string(name: 'image', value: "${image}")]
                        } catch (Exception e) {
                            slackSend color: 'error', message: "[FALHA] Não foi possível subir o container na produção ${BUILD_URL}"
                            sh 'echo $e'
                            currentBuild.result = 'ABORTED'
                            error('Erro')
                        }
                }
            }
        }
        stage('Notificando o usuário do deploy produção') {
            steps {
                slackSend color: 'good', message: "[SUCESSO] Novo build de produção disponível em: http://138.36.3.60:8083/"
            }
        }
        */
    }
}