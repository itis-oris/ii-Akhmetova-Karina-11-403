<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Регистрация — CakeShop</title>
    <#include "fragments/styles.ftlh">
</head>
<body>
<#include "fragments/header.ftlh">
<main class="main">
    <div class="container auth-card card">
        <div class="page-intro" style="margin-bottom:14px;">
            <h1 style="margin:0;">Регистрация</h1>
        </div>

        <#if registerError?has_content><div class="alert error">${registerError}</div></#if>

        <form method="post" action="/register">
            <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">

            <label>Имя пользователя<input name="username" value="${(registerRequest.username)!''}" required minlength="3" maxlength="50" autocomplete="username"></label>
            <label>Email<input name="email" type="email" value="${(registerRequest.email)!''}" required autocomplete="email"></label>
            <label>Пароль<input name="password" type="password" required minlength="6" autocomplete="new-password"></label>

            <button class="btn" type="submit" style="width:100%;margin-top:10px;">Создать аккаунт</button>
        </form>

        <p class="muted" style="margin-top:18px;">Уже есть аккаунт? <a href="/login">Войти</a></p>
    </div>
</main>
<#include "fragments/scripts.ftlh">
</body>
</html>
