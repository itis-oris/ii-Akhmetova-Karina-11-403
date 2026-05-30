<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Каталог — ${confectioner.name}</title>
    <#include "fragments/styles.ftlh">
</head>
<body>
<#include "fragments/header.ftlh">
<main class="main">
    <div class="container">

        <div class="breadcrumb">
            <a href="/catalog">Каталог</a>
            <span aria-hidden="true">/</span>
            <span>${confectioner.name}</span>
        </div>

        <header class="page-intro confectioner-hero">
            <div class="confectioner-hero-text">
                <h1>${confectioner.name}</h1>
                <#if confectioner.shop??>
                    <p class="muted" style="margin:4px 0 0;font-size:0.95rem;">${confectioner.shop.branchTitle}</p>
                    <p class="lead">${confectioner.shop.address}</p>
                </#if>
                <#if confectioner.phone?? && confectioner.phone?has_content>
                    <p class="lead">Тел.: ${confectioner.phone}.</p>
                </#if>
            </div>
        </header>

        <#if success??><div class="alert success">${success}</div></#if>
        <#if error??><div class="alert error">${error}</div></#if>

        <div class="catalog-panels">
            <div class="card card-toolbar">
                <h3>Поиск</h3>
                <div class="row" style="align-items:flex-end;margin-top:8px;">
                    <label style="flex:1;min-width:180px;margin:0;">Название, категория или артикул
                        <input id="q" type="text" placeholder="Например, мусс" autocomplete="off">
                    </label>
                    <button class="btn" type="button" id="applyFilters">Показать</button>
                </div>
            </div>

        </div>

        <h2 style="margin-top:28px;">Торты</h2>
        <p class="muted" style="margin:0 0 6px;">«Заказать» доступно после входа.</p>
        <p id="catalogSearchStatus" class="muted" style="margin:0 0 14px;min-height:1.4em;font-weight:600;" aria-live="polite"></p>

        <div id="cakesGrid" class="grid tiles-grid-mt" data-confectioner-id="${confectioner.id}">
            <#list cakes as cake>
                <div class="tile-cake">
                    <div class="tile-title">${cake.name}</div>
                    <div class="tile-sub">
                        Арт.: <#if cake.sku?? && cake.sku?has_content>${cake.sku}<#else>—</#if>
                    </div>
                    <div class="tile-sub muted">Категория: ${(cake.category)!"—"} · Вес: <#if cake.netWeightG??>${cake.netWeightG} г<#else>—</#if></div>
                    <div class="tile-sub cake-rating-line">
                        <#if cake.averageRating??>
                            ★ ${cake.averageRating?string["0.#"]} / 5
                        <#else>
                            <span class="muted">Нет оценок</span>
                        </#if>
                    </div>
                    <div class="tile-sub"><span class="price-tag">${cake.price} ₽</span></div>
                    <form method="post" action="/orders" style="margin-top:auto;">
                        <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
                        <input type="hidden" name="cakeId" value="${cake.id}">
                        <input type="hidden" name="confectionerId" value="${confectioner.id}">
                        <button class="btn" type="submit" style="width:100%;margin-top:12px;">Заказать</button>
                    </form>
                </div>
            </#list>
        </div>

        <section class="card catalog-reviews" style="margin-top:36px;">
            <h2 style="margin-top:0;">Отзывы</h2>
            <#assign revs = reviews![] />
            <#if (revs?size == 0)>
                <p class="muted">Пока нет отзывов.</p>
            <#else>
                <#list revs as rv>
                    <div class="review-card">
                        <div class="review-meta">
                            <span class="review-stars">★ ${rv.rating} / 5</span>
                            · ${rv.cake.name}
                            · ${rv.customer.username}
                            <#if rv.createdAt??>
                                <#-- LocalDateTime / Timestamp или ISO-строка -->
                                · <#if rv.createdAt?is_date_like>${rv.createdAt?string["dd.MM.yyyy HH:mm"]}<#else><#assign caIso = rv.createdAt?string />${caIso?substring(0,19)?replace("T", " ")?datetime("yyyy-MM-dd HH:mm:ss")?string("dd.MM.yyyy HH:mm")}</#if>
                            </#if>
                        </div>
                        <#if rv.comment?? && rv.comment?has_content>
                            <p style="margin:0;">${rv.comment}</p>
                        </#if>
                    </div>
                </#list>
            </#if>

            <hr style="border:0;border-top:1px solid var(--border);margin:22px 0;">

            <#if !(isAuthenticated?? && isAuthenticated)>
                <p class="muted" style="margin:0;"><a href="/login">Войти</a>, чтобы оставить отзыв.</p>
            <#elseif (cakes?size == 0)>
                <p class="muted" style="margin:0;">Отзывы появятся после добавления тортов.</p>
            <#else>
                <h3 style="margin:0 0 12px;">Новый отзыв</h3>
                <form method="post" action="/catalog/reviews">
                    <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
                    <input type="hidden" name="confectionerId" value="${confectioner.id}">
                    <label>Торт
                        <select name="cakeId" required>
                            <#list cakes as ck>
                                <option value="${ck.id}">${ck.name}</option>
                            </#list>
                        </select>
                    </label>
                    <label>Оценка
                        <select name="rating" required>
                            <option value="5">5</option>
                            <option value="4">4</option>
                            <option value="3">3</option>
                            <option value="2">2</option>
                            <option value="1">1</option>
                        </select>
                    </label>
                    <label>Комментарий
                        <textarea name="comment" rows="3" maxlength="2000" placeholder="По желанию"></textarea>
                    </label>
                    <button class="btn" type="submit" style="margin-top:10px;">Отправить</button>
                </form>
            </#if>
        </section>
    </div>
</main>
<script>
    window.csrfData = { parameterName: "${csrfParameterName!''}", token: "${csrfToken!''}" };
</script>
<#include "fragments/scripts.ftlh">
</body>
</html>
