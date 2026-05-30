<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Админ • Кондитеры</title>
    <#include "../fragments/styles.ftlh">
</head>
<body>
<#include "../fragments/header.ftlh">
<main class="main">
    <div class="container">
        <header class="page-intro">
            <h1>Кондитеры</h1>
        </header>

        <#if success??><div class="alert success">${success}</div></#if>
        <#if error??><div class="alert error">${error}</div></#if>

        <p style="margin-bottom:12px;"><a class="btn btn-ghost" href="/admin/cakes">← Админ</a></p>

        <div class="card">
            <#list confectioners as cf>
                <div class="row card" style="margin:10px 0;padding:14px;">
                    <div>
                        <span class="muted" style="display:inline-block;min-width:2em;font-weight:700;">${cf_index + 1}.</span>
                        ${cf.name}
                        <#if cf.shop??>
                            <div class="muted" style="font-size:0.92rem;margin-top:6px;"><strong>${cf.shop.branchTitle}</strong></div>
                            <div class="muted" style="font-size:0.92rem;margin-top:4px;">${cf.shop.address}</div>
                        </#if>
                        <#if cf.phone?? && cf.phone?has_content>
                            <div class="muted" style="font-size:0.92rem;margin-top:4px;">${cf.phone}</div>
                        </#if>
                    </div>
                    <div class="row" style="gap:12px;">
                        <a class="link-btn" href="/admin/confectioners/${cf.id}/edit">Править</a>
                        <form method="post" action="/admin/confectioners/${cf.id}/delete" style="margin:0;">
                            <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
                            <button class="link-btn" type="submit">Удалить</button>
                        </form>
                    </div>
                </div>
            </#list>
        </div>
    </div>
</main>
<#include "../fragments/scripts.ftlh">
</body>
</html>
