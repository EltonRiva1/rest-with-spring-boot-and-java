# REST API com Spring Boot e Java

![Badge](https://img.shields.io/badge/Status-%20Concluído-green) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.12-brightgreen)

Uma API RESTful desenvolvida com **Spring Boot** e **Java**, utilizando boas práticas e padrões modernos para construção de serviços web escaláveis e performáticos.

## 🚀 Tecnologias Utilizadas

- **Java 8+**
- **Spring Boot 3.2.12**
- **Spring Web**
- **Spring Data JPA**
- **Hibernate**
- **Swagger (OpenAPI)**
- **MySQL**
- **Maven**
- **Docker**

## 📌 Funcionalidades

- 🌐 **CRUD completo de recursos RESTful**
- 🔍 **Filtros e paginação**
- 🔑 **Autenticação e Autorização com Spring Security**
- 🛠️ **Integração com Swagger para documentação da API**
- 📊 **Monitoramento com Actuator**
- 📌 **Testes unitários e de integração**

## 🎯 Como Executar o Projeto

### 📌 Pré-requisitos
Antes de iniciar, você precisará ter instalado:
- Java 8+
- Maven
- MySQL

### 🔧 Passos para execução
1. Clone o repositório:
   ```sh
   git clone https://github.com/EltonRiva1/rest-with-spring-boot-and-java.git
   ```
2. Acesse a pasta do projeto:
   ```sh
   cd rest-with-spring-boot-and-java
   ```
3. Configure o banco de dados no arquivo `application.yml`:
   ```yaml
   spring:
     application:
       name: rest-with-spring-boot-and-java
     datasource:
       driver-class-name: com.mysql.cj.jdbc.Driver
       url: jdbc:mysql://localhost:3306/rest_with_spring_boot?useTimezone=true&serverTimezone=UTC
       username: root
       password: root
     jpa:
       hibernate:
         ddl-auto: update

4. Compile e execute o projeto:
   ```sh
   mvn spring-boot:run
   ```
5. Acesse a API no navegador ou via Postman:
   ```
   http://localhost:8080/api
   ```
6. Para visualizar a documentação da API (Swagger):
   ```
   http://localhost:8080/swagger-ui.html
   ```
   
## 🛠️ Contribuindo
1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature-minha-feature`)
3. Commit suas alterações (`git commit -m 'Adicionando nova funcionalidade'`)
4. Faça um push para a branch (`git push origin feature-minha-feature`)
5. Abra um Pull Request

---

🔹 Desenvolvido por [Elton Riva](https://github.com/EltonRiva1) 🚀

