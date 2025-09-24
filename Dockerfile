# Estágio 1: Build da aplicação
FROM maven:4.0.0-rc-4-amazoncorretto-21 AS build
WORKDIR /app

# Copiar arquivos de configuração Maven
COPY pom.xml .

# Baixar dependências (cache layer)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar aplicação
RUN mvn clean package -DskipTests -B

# Estágio 2: Runtime
FROM openjdk:21-jdk-slim

# Metadados
LABEL maintainer="tabajara@mail.com"
LABEL description="Sistema de Login com Spring Boot - Auto Build"

# Instalar curl para health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Diretório de trabalho
WORKDIR /app

# Copiar JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Mudar proprietário
RUN chown -R appuser:appuser /app

# Usar usuário não-root
USER appuser

# Expor porta
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8081/api/auth/health || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]