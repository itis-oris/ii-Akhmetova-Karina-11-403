<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Профиль — CakeShop</title>
    <#include "fragments/styles.ftlh">
</head>
<body>
<#include "fragments/header.ftlh">
<main class="main">
    <div class="container">

        <header class="page-intro">
            <h1>Профиль</h1>
        </header>

        <#if success??><div class="alert success">${success}</div></#if>

        <div class="card">
            <h2>Данные</h2>
            <p class="muted" style="margin-top:6px;margin-bottom:0;">
                <strong>Логин:</strong> ${customer.username}<br>
                <strong>Email:</strong> ${customer.email}
            </p>

            <#if !(isAdmin?? && isAdmin)>
                <form method="post" action="/profile/update" style="margin-top:18px;">
                    <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
                    <label>Телефон <input name="phone" value="${customer.phone!''}"></label>
                    <label>Адрес
                        <input id="profileAddress" name="address" value="${customer.address!''}" placeholder="Казань, ул. Баумана, 19" maxlength="512" autocomplete="street-address">
                    </label>
                    <p class="muted" style="margin:0 0 12px;font-size:0.9rem;">Укажите город, улицу и дом. После «Сохранить» страница обновится, карта пересчитается.</p>
                    <button class="btn" type="submit">Сохранить</button>
                </form>
            </#if>
        </div>

        <#if !(isAdmin?? && isAdmin)>
        <div class="card" id="mapSection">
            <h2>Ближайшая кондитерская</h2>
            <p id="mapNearestName" class="map-nearest-name" style="margin-top:10px;"></p>
            <p id="mapShopAddress" class="map-summary-address" style="margin:6px 0 0;"></p>
            <p id="mapMessage" class="muted" style="margin:10px 0 0;font-size:0.92rem;"></p>
            <p class="muted" style="margin:14px 0 0;font-size:0.9rem;">
                <a id="mapOpenTextLink" href="#" target="_blank" rel="noopener noreferrer">Открыть карту в Яндекс.Картах →</a>
            </p>
            <div id="mapBranchTeamWrap" style="margin-top:18px;">
                <ul id="mapBranchTeam" class="map-branch-team" style="list-style:none;padding:0;margin:0;"></ul>
            </div>
        </div>

        <div class="card">
            <h2>Заказы</h2>
            <#if (orders?size == 0)>
                <p class="muted" style="margin-top:12px;">Заказов пока нет.</p>
            <#else>
                <#list orders as o>
                    <div class="row card" style="margin:10px 0;padding:14px;">
                        <div>
                            <div><strong>#${o.id}</strong> · ${o.status} · <span class="price-tag">${o.totalPrice} ₽</span></div>
                            <div class="muted" style="margin-top:8px;font-size:0.94rem;line-height:1.45;">
                                ${o.cake.name}<br>${o.confectioner.name}
                            </div>
                        </div>
                        <form method="post" action="/orders/delete">
                            <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
                            <input type="hidden" name="orderId" value="${o.id}">
                            <button class="link-btn" type="submit">Удалить</button>
                        </form>
                    </div>
                </#list>
            </#if>
        </div>
        </#if>

    </div>
</main>
<#include "fragments/scripts.ftlh">
</body>
</html>
