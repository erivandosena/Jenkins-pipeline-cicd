#!/bin/bash

# Validar Dockerfile com Hadolint
docker run --rm -i hadolint/hadolint < Dockerfile

# Baixar o scanner Sonarqube
wget https://binaries.sonarsource.com/Distribution/sonar-scanner-cli/sonar-scanner-cli-4.7.0.2747-linux.zip

# Descompactar o Scanner
apt-get install -y unzip nodejs
unzip sonar-scanner-cli-4.7.0.2747-linux.zip
export PATH="sonar-scanner-4.7.0.2747-linux/bin:$PATH"

# Rodar o scanner
sonar-scanner \
  -Dsonar.projectKey=Projeto-Jenkins \
  -Dsonar.sources=. \
  -Dsonar.host.url=http://138.36.3.60:9002 \
  -Dsonar.login=<SONARQUBE-TOKEN-PROJETO>