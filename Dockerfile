# Use uma imagem leve com Java 21 (ou sua versão)
FROM eclipse-temurin:21-jdk-alpine

# Cria diretório de trabalho
WORKDIR /app

# Copia o JAR gerado para dentro do container
COPY target/*.jar app.jar

# Expõe a porta 8080
EXPOSE 8080

# Comando para rodar o app
ENTRYPOINT ["java", "-jar", "app.jar"]
