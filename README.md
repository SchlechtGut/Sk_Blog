<h1 align="center">Sk_Blog</h1> 

<p align="center">

<img src="">

<img src="https://img.shields.io/badge/made%20by-SchlechtGut-red">

<img src="https://img.shields.io/badge/Java-95.8%25-green">

</p>

## Description

This project is a simple single page application. Back end was done with the help of **Spring Boot**. 

The app has authentication and authorization which has been implemented by Spring Security. Additional protection was added with captcha and confirm links sent to email.
Also, with Spring Data the project has been connected to the DataBase on MySQL. 

The app is a blog in which users can share their posts. Beside users there also moderators who have more rights in the blog and thus are able to fulfill their functions.

The frontend however was there in the first place so all the work was about backend only.

<p align="center">
<img src="https://media.giphy.com/media/184evEfMtF1ilDS5Qp/giphy.gif" width="80%">
</p>

## Project setup

Environment variables:

DATABASE_URL - DB connection with login and password 

```
mvn spring-boot:run -Dspring-boot.run.arguments="--DATABASE_URL=mysql://sql11503529:SVjZn6rkyA@sql11.freemysqlhosting.net:3306/sql11503529?useUnicode=true&characterEncoding=utf-8&reconnect=true"
```

Also a link to heroku deploy:

https://aytasov-java-blog-skillbox.herokuapp.com/




