<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка — CakeShop</title>
    <#include "fragments/styles.ftlh">
</head>
<body>
<#include "fragments/header.ftlh">
<main class="main">
    <div class="container auth-card card">
        <h1>Ошибка</h1>
        <p class="lead" style="margin-top:10px;">${status!500}</p>
        <div class="alert error" role="alert">${errorMessage!'Что-то пошло не так.'}</div>
        <p style="margin-top:20px;"><a class="btn" href="/">На главную</a></p>
    </div>
</main>
<#include "fragments/scripts.ftlh">
</body>
</html>
