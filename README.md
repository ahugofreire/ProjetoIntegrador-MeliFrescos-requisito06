# ProjetoIntegrador - Mercado Livre: Frescos (Requisito 06)

Este Projeto adiciona a funcionalidade de agendamento para as entregas dos pedidos.

O projeto principal se destina a simular uma nova modalidade de armazenamento, transporte e comercialização de produtos frescos, congelados e refrigerados do Mercado Livre através de uma API REST em Java com Spring Boot e suas respectivas dependências.

### Tecnologias Utilizadas:
- Java 11;
- Spring Security e Token JWT;
- Spring Validations;
- Spring Data JPA;
- Banco de Dados relacional Postgres (local);
- Docker 
### Instruções para a instalação:

Para iniciar o projeto será necessário executar o docker.

Dentro do diretorio raiz do projeto executar o comando abaixo.

```sh
docker-compose up -d 
```
Para executar o projeto será necessárioconfigurar as variavies de ambiente, na IDE ou no sistema operacional.
```sh

HOST=jdbc:postgresql://localhost:5432/pidb;USERNAME=(seu_nome_de_usuário);PASSWORD=(sua_senha_definida)
```

### Collection com os End-points no Postman:

Encontra-se dentro do projeto, no diretório abaixo:

```sh
src/main/resources/requisito-06.postman_collection.json
```

### Documentação com swagger
```sh
http://localhost:8080/api/v1/swagger-ui.html
http://localhost:8080/api/v1/v2/api-docs
```
### Diagramas de Classe

![R6](https://user-images.githubusercontent.com/83099706/167030128-886b0e84-b345-493b-a321-8947df9999f4.png)
### Diagrama de Entidade-Relacionamento
![ERD](https://user-images.githubusercontent.com/83099706/167040065-661b4606-9f93-4af0-a5df-aeafc17058b0.png)

### Documentação, Referencial utilizados e Cronologia dos requisitos:

[Enunciado Base](https://drive.google.com/file/d/1bBOM49bxqRR7apxP3sgV7_LRiTq9xQD2/view)

[Requisito 1](https://drive.google.com/file/d/1rbT3upYAwN-CrOVtze0M2Fq7Cobuj7FD/view) (Início em: 22/04/22, Término em: 27/04/22)

[Requisito 2](https://drive.google.com/file/d/1M66St3F6TwWJ6WG_s1in75_bMyeKb8PM/view) (Início em: 26/04/22, Término em: 02/05/22)

[Requisito 3](https://drive.google.com/file/d/1GnTl6sHhdvyKjR0oz0nXlyvzH-oW_2Jv/view) (Início em: 28/04/22, Término em: 29/04/22)

[Requisito 4](https://drive.google.com/file/d/1kNZLztafr2tXuDU24W9xwUu09va2kMP0/view) (Início em: 29/04/22, Término em: 02/05/22)

[Requisito 5](https://drive.google.com/file/d/1yiEzdwI87K7AO9bgPffHbb0DPjVKM-oP/view) (Início em: 29/04/22, Término em: 03/05/22)

[Requisito 6](https://drive.google.com/file/d/1zlRtIPjK4r0WdrzFs7LIVA_8Q5HyDgXz/view) (Início em: 03/05/22, Término em: 06/05/22)
