<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход — CakeShop</title>
    <#include "fragments/styles.ftlh">
</head>
<body>
<#include "fragments/header.ftlh">
<main class="main">
    <div class="container auth-card card">
        <div class="page-intro" style="margin-bottom:14px;">
            <h1 style="margin:0;">Вход</h1>
        </div>
        <#if success??><div class="alert success">${success}</div></#if>
        <#if loginError?? && loginError><div class="alert error">Неверный email или пароль.</div></#if>
        <#if logoutOk?? && logoutOk><div class="alert success">Вы вышли из аккаунта.</div></#if>

        <form method="post" action="/login">
            <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
            <label>Email<input name="username" type="email" required autocomplete="email"></label>
            <label>Пароль<input name="password" type="password" required autocomplete="current-password"></label>
            <button class="btn" type="submit" style="width:100%;margin-top:8px;">Войти</button>
        </form>

        <p class="muted" style="margin-top:18px;">Нет аккаунта? <a href="/register">Зарегистрироваться</a></p>
    </div>
</main>
<#include "fragments/scripts.ftlh">
</body>
</html>
