/* Jenkinsfile (Declarative Pipeline) */

pipeline {
    agent any
    parameters {
        string(defaultValue: "", name: 'IMAGE', description: 'Valor parametrizado da imagem a partir da pipeline de desenvolvimento.')
    }
    stages {
        stage('Aplicar deploy em produção?') {
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
                        try {
                            sh """docker run -d --name app-exemplo-mobead-prod --restart always -p 8083:80 ${params.IMAGE}"""
                        } catch (Exception e) {
                            sh 'echo $e'
                            sh 'docker rm -f app-exemplo-mobead-prod'
                            sh """docker run -d --name app-exemplo-mobead-prod --restart always -p 8083:80 ${params.IMAGE}"""
                        }
                    } catch (Exception e) {
                        slackSend color: 'error', message: "[FALHA] Não foi possível subir o container na produção ${BUILD_URL}"
                        sh 'echo $e'
                        currentBuild.result = 'ABORTED'
                        error('Erro')
                    }
                }
            }
        }
        stage('Notificando o usuário') {
            steps {
                slackSend color: 'good', message: "[SUCESSO] Novo build de produção disponível em: http://138.36.3.60:8083/"
            }
        }
    }
}