<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Админ • Торт</title>
    <#include "../fragments/styles.ftlh">
</head>
<body>
<#include "../fragments/header.ftlh">


<main class="main">
    <div class="container auth-card card" style="max-width:720px;">
        <h1 style="margin-bottom:8px;"><#if isEdit?? && isEdit>Правка торта<#else>Новый торт</#if></h1>

        <#if error??><div class="alert error">${error}</div></#if>

        <form method="post" action="<#if isEdit?? && isEdit>/admin/cakes/${cakeId}<#else>/admin/cakes</#if>">
            <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
            <label>Кондитер
                <select name="confectionerId" required>
                    <option value="" disabled<#if !cakeRequest.confectionerId??> selected</#if>>—</option>
                    <#list confectioners as k>
                        <option value="${k.id}"<#if cakeRequest.confectionerId?? && cakeRequest.confectionerId == k.id> selected</#if>>${k.name}</option>
                    </#list>
                </select>


            </label>
            <label>Название
                <input name="name" value="${(cakeRequest.name)!''}" required maxlength="180">
            </label>
            <label>Артикул
                <input name="sku" value="${(cakeRequest.sku)!''}" maxlength="64" placeholder="CAKE-042" required>
            </label>
            <label>Категория
                <input name="category" value="${(cakeRequest.category)!''}" required maxlength="120">
            </label>
            <label>Вес, г
                <input name="netWeightG" type="number" step="1" min="1" value="<#if netWeightInputValue??>${netWeightInputValue}<#elseif cakeRequest.netWeightG??>${cakeRequest.netWeightG?c}</#if>" required>
            </label>
            <label>Цена, ₽
                <input name="price" type="number" step="0.01" min="0.01" value="<#if priceInputValue??>${priceInputValue}<#elseif cakeRequest.price??>${cakeRequest.price?c}</#if>" required>
            </label>


            <#if isEdit?? && isEdit && cakeAverageRating??>
                <p class="muted" style="margin:0 0 12px;">
                    Рейтинг: <#if cakeAverageRating?? && cakeAverageRating?is_number>★ ${cakeAverageRating?string["0.#"]} / 5<#else>—</#if>
                </p>

            </#if>
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
