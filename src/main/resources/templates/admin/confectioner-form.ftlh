<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Админ • Кондитер</title>
    <#include "../fragments/styles.ftlh">
</head>
<body>
<#include "../fragments/header.ftlh">
<main class="main">
    <div class="container auth-card card" style="max-width:560px;">
        <h1 style="margin-bottom:8px;"><#if isEdit?? && isEdit>Правка кондитера<#else>Новый кондитер</#if></h1>

        <#if error??><div class="alert error">${error}</div></#if>

        <form method="post" action="<#if isEdit?? && isEdit>/admin/confectioners/${confectionerId}<#else>/admin/confectioners</#if>">
            <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
            <label>Имя мастера
                <input name="name" value="${(request.name)!''}" required minlength="1" maxlength="120">
            </label>
            <label>Точка сети (адрес на карте)
                <select name="shopId" required>
                    <#list shops as sh>
                        <option value="${sh.id}"
                            <#if request.shopId?? && request.shopId?string == sh.id?string> selected</#if>>
                            ${sh.branchTitle} — ${sh.address}
                        </option>
                    </#list>
                </select>
            </label>
            <p class="muted" style="margin:6px 0 10px;font-size:0.9rem;">
                Кондитер привязан к одной из трёх точек нашей кондитерской в городе.</p>
            <label>Телефон
                <input name="phone" value="${(request.phone)!''}" placeholder="+7" maxlength="32">
            </label>
            <div class="row" style="margin-top:16px;">
                <button class="btn" type="submit">Сохранить</button>
                <a class="btn btn-ghost" href="/admin/cakes">Отмена</a>
            </div>
        </form>
    </div>
</main>
<#include "../fragments/scripts.ftlh">
</body>
</html>
