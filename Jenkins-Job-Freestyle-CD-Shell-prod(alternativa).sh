#!/bin/sh

# Executa o Shell
{
	docker run -d --name app-exemplo-mobead-prod --restart always -p 8083:80 $image
} || { #cath
	docker rm -f app-exemplo-mobead-prod
	docker run -d --name app-exemplo-mobead-prod --restart always -p 8083:80 $image
}