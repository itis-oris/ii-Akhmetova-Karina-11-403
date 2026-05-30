<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Админ</title>
    <#include "../fragments/styles.ftlh">
</head>
<body>
<#include "../fragments/header.ftlh">
<main class="main">
    <div class="container">
        <header class="page-intro">
            <h1>Админ</h1>
        </header>

        <#if success??><div class="alert success">${success}</div></#if>
        <#if error??><div class="alert error">${error}</div></#if>

        <div class="row card" style="flex-wrap:wrap;gap:12px;padding:16px 18px;margin-bottom:16px;">
            <a class="btn" href="/admin/confectioners/new">+ Кондитер</a>
            <a class="btn btn-ghost" href="/admin/cakes/new">+ Торт</a>
            <a class="btn btn-ghost" href="/admin/confectioners">Список кондитеров</a>
        </div>


        <div class="card" style="margin-bottom:22px;">
            <h3 style="margin-top:4px;">Торты по кондитеру</h3>
            <form method="get" action="/admin/cakes" class="row" style="align-items:flex-end;flex-wrap:wrap;gap:12px;margin:12px 0 4px;">
                <label style="flex:1;min-width:220px;margin:0;">Кондитер
                    <select name="pickedConfectionerId">
                        <option value=""<#if !pickedConfectionerId??> selected</#if>>—</option>
                        <#list confectioners as k>
                            <option value="${k.id}"
                                <#if pickedConfectionerId?? && pickedConfectionerId?long == k.id>selected</#if>>${k.name}</option>
                        </#list>
                    </select>
                </label>
                <button class="btn" type="submit" style="margin-top:22px;">Показать</button>
            </form>


            <#if pickerError??>
                <p class="alert error" style="margin-top:12px;padding:10px 12px;">${pickerError}</p>
            </#if>


            <#if pickedConfectionerId?? && pickedConfectionerName?? && !(pickerError??)>
                <p class="muted" style="margin:14px 0 12px;"><strong>${pickedConfectionerName}</strong></p>


                <#if (cakes?size == 0)>
                    <p class="muted">Нет тортов. <a href="/admin/cakes/new">Добавить</a></p>
                <#else>
                    <#list cakes as c>
                        <div class="row card" style="margin:10px 0;padding:14px;">
                            <div>
                                <strong>#${c.id}</strong> · ${c.name}
                                <span class="price-tag" style="margin-left:8px;">${c.price} ₽</span>
                                <div class="muted" style="font-size:0.88rem;margin-top:4px;">
                                    ${(c.sku)!""} · ${(c.category)!""} · <#if c.netWeightG??>${c.netWeightG} г<#else>—</#if>
                                </div>
                                <div class="muted" style="font-size:0.88rem;margin-top:2px;">
                                    <#if c.averageRating??>★ ${c.averageRating?string["0.#"]}<#else>—</#if>
                                </div>
                            </div>
                            <div class="row" style="gap:12px;">
                                <a class="link-btn" href="/admin/cakes/${c.id}/edit">Править</a>
                                <form method="post" action="/admin/cakes/${c.id}/delete" style="margin:0;">
                                    <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
                                    <button class="link-btn" type="submit">Удалить</button>
                                </form>
                            </div>
                        </div>
                    </#list>
                </#if>
            <#elseif !(pickerError??)>
                <p class="muted" style="margin-top:16px;">Выберите кондитера.</p>
            </#if>
        </div>
    </div>
</main>
<#include "../fragments/scripts.ftlh">
</body>
</html>
