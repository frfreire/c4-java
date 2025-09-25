# Sistema de Autenticação JWT - Tabajara Auth API

Sistema de autenticação desenvolvido em Spring Boot com JWT, oferecendo endpoints para registro, login e gerenciamento de usuários.

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Token)
- Maven
- Swagger/OpenAPI 3

## Pré-requisitos

Antes de executar o sistema, certifique-se de ter instalado:

- **Java 21** ou superior
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Git**

## Configuração do Ambiente

### 1. Clone do Repositório

```bash
git clone [https://github.com/frfreire/c4-java.git](https://github.com/frfreire/c4-java.git)
cd login
```

### 2. Configuração do Banco de Dados

#### 2.1 Instalar PostgreSQL

No Ubuntu/Debian:
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
```

No Windows: Baixe e instale o PostgreSQL do site oficial.

No macOS (com Homebrew):
```bash
brew install postgresql
brew services start postgresql
```

#### 2.2 Criar Banco de Dados e Usuário

Acesse o PostgreSQL como superusuário:
```bash
sudo -u postgres psql
```

Execute os seguintes comandos SQL:
```sql
CREATE DATABASE login_db;
CREATE USER login_user WITH ENCRYPTED PASSWORD 'login_pass';
GRANT ALL PRIVILEGES ON DATABASE login_db TO login_user;
ALTER USER login_user CREATEDB;
\q
```

#### 2.3 Verificar Conectividade

Teste a conexão com o banco:
```bash
psql -h localhost -U login_user -d login_db
```

### 3. Configuração de Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto ou configure as variáveis no sistema:

```bash
# JWT Configuration
export JWT_SECRET=minha-chave-secreta-super-segura-deve-ter-pelo-menos-256-bits-para-funcionar-corretamente

# Database Configuration (opcional se usar as configurações padrão)
export DATABASE_URL=jdbc:postgresql://localhost:5432/login_db
export DATABASE_USERNAME=login_user
export DATABASE_PASSWORD=login_pass
```

### 4. Configuração do Projeto

#### 4.1 Validar Configuração do application.yaml

Verifique se o arquivo `src/main/resources/application.yaml` está com as configurações corretas:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/login_db
    username: login_user
    password: login_pass
    driver-class-name: org.postgresql.Driver
```

## Executando o Sistema

### 1. Instalação das Dependências

```bash
mvn clean install
```

### 2. Executar Testes

```bash
mvn test
```

### 3. Executar a Aplicação

#### Opção 1: Via Maven
```bash
mvn spring-boot:run
```

#### Opção 2: Via JAR
```bash
mvn clean package
java -jar target/login-1.0.0.jar
```

#### Opção 3: Com Profile Específico
```bash
# Para desenvolvimento
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Para testes
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### 4. Verificar se a Aplicação Está Funcionando

A aplicação estará disponível em: `http://localhost:8081`

#### Endpoints de Verificação:

**Health Check:**
```bash
curl http://localhost:8081/api/auth/health
```

**Swagger UI:**
Acesse: `http://localhost:8081/swagger-ui.html`

**OpenAPI JSON:**
Acesse: `http://localhost:8081/v3/api-docs`

## Endpoints da API

### Autenticação

#### 1. Registrar Usuário
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "usuario_teste",
  "email": "teste@email.com",
  "password": "senha123"
}
```

#### 2. Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "usuario_teste",
  "password": "senha123"
}
```

Resposta:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "username": "usuario_teste",
  "email": "teste@email.com",
  "role": "USUARIO",
  "expiraEm": 86400000
}
```

#### 3. Perfil do Usuário (Autenticado)
```bash
GET /api/auth/profile
Authorization: Bearer <token>
```

#### 4. Logout
```bash
POST /api/auth/logout
Authorization: Bearer <token>
```

## Perfis de Configuração

### Desenvolvimento (dev)
- Usa PostgreSQL local
- Logs detalhados
- DDL auto update

### Teste (test)
- Usa H2 in-memory database
- Console H2 habilitado
- DDL create-drop

### Produção (prod)
- Configurações via variáveis de ambiente
- Logs otimizados
- DDL validate

## Estrutura do Projeto

```
src/main/java/com/tabajara/login/
├── config/
│   ├── OpenApiConfig.java
│   └── SecurityConfig.java
├── controller/
│   └── AutenticacaoController.java
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── RegisterRequest.java
├── exception/
│   └── GlobalExceptionHandler.java
├── model/
│   ├── Role.java
│   └── Usuario.java
├── repository/
│   └── UsuarioRepository.java
├── security/
│   ├── JwtAuthenticationEntryPoint.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
├── service/
│   ├── AutenticacaoService.java
│   ├── IAutenticacaoService.java
│   └── UsuarioDetalheService.java
└── LoginApplication.java
```

## Recursos e Funcionalidades

### Segurança
- Autenticação JWT stateless
- Criptografia de senhas com BCrypt
- Controle de acesso baseado em roles
- CORS configurado para desenvolvimento
- Tratamento centralizado de exceções

### Monitoramento
- Spring Boot Actuator
- Health checks
- Métricas de aplicação
- Documentação automática com Swagger

### Validações
- Validação de dados de entrada
- Tratamento de erros de validação
- Mensagens de erro personalizadas

## Testando a API

### Usando cURL

**1. Registrar um novo usuário:**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**2. Fazer login:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**3. Acessar perfil (substitua TOKEN pelo token recebido no login):**
```bash
curl -X GET http://localhost:8081/api/auth/profile \
  -H "Authorization: Bearer TOKEN"
```

### Usando Postman

1. Importe a coleção através da documentação Swagger em `http://localhost:8081/swagger-ui.html`
2. Configure uma variável de ambiente `baseUrl` com valor `http://localhost:8081`
3. Após o login, configure uma variável `authToken` com o token JWT recebido

## Troubleshooting

### 1. Erro de Conexão com Banco de Dados

```bash
# Verificar se PostgreSQL está rodando
sudo systemctl status postgresql

# Reiniciar PostgreSQL se necessário
sudo systemctl restart postgresql

# Verificar logs do PostgreSQL
sudo tail -f /var/log/postgresql/postgresql-*.log
```

### 2. Erro de Porta em Uso

```bash
# Verificar qual processo está usando a porta 8081
sudo netstat -tulpn | grep 8081

# Alterar a porta no application.yaml ou usar variável de ambiente
export SERVER_PORT=8082
```

### 3. Problemas com JWT

```bash
# Verificar se a chave JWT está configurada corretamente
echo $JWT_SECRET

# A chave deve ter pelo menos 256 bits (32 caracteres)
```

### 4. Problemas de Memória

```bash
# Executar com mais memória se necessário
java -Xmx1024m -jar target/login-1.0.0.jar

# Ou via Maven
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx1024m"
```

### 5. Logs Detalhados para Debug

Adicione no `application.yaml`:
```yaml
logging:
  level:
    com.tabajara.login: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

## Considerações de Segurança

### Desenvolvimento
- Use senhas fortes para o banco de dados
- Mantenha a chave JWT segura e complexa
- Configure CORS adequadamente para seu ambiente

### Produção
- Use HTTPS sempre
- Configure variáveis de ambiente para credenciais
- Implemente rate limiting
- Configure logs de auditoria
- Use um banco de dados dedicado
- Configure backup automático do banco
- Monitore tentativas de login suspeitas

## Deploy

### Docker (Opcional)

Crie um `Dockerfile`:
```dockerfile
FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/login-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

Execute:
```bash
docker build -t tabajara-auth .
docker run -p 8081:8081 tabajara-auth
```

### Variáveis de Ambiente para Produção

```bash
JWT_SECRET=sua-chave-super-secreta-de-producao
DATABASE_URL=jdbc:postgresql://seu-host:5432/login_db
DATABASE_USERNAME=seu-usuario
DATABASE_PASSWORD=sua-senha-segura
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8081
```

## Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Add nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

### Padrões de Código

- Siga as convenções do Java
- Use nomes descritivos para variáveis e métodos
- Documente classes e métodos públicos
- Escreva testes para novas funcionalidades
- Mantenha a cobertura de testes acima de 80%

---
