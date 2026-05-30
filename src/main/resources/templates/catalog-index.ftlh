<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Каталог — CakeShop</title>
    <#include "fragments/styles.ftlh">
</head>
<body>
<#include "fragments/header.ftlh">
<main class="main">
    <div class="container">
        <header class="page-intro">
            <h1>Каталог</h1>
            <p class="muted" style="margin-top:8px;">Одна кондитерская, три точки в Казани.</p>
        </header>

        <#list shops as sh>
            <section class="card" style="margin-bottom:28px;">
                <h2 style="margin:0 0 6px;font-size:1.15rem;">${sh.branchTitle}</h2>
                <p class="muted" style="margin:0 0 16px;font-size:0.92rem;line-height:1.4;">${sh.address}</p>
                <div class="grid">
                    <#list confectioners as c>
                        <#if c.shop.id == sh.id>
                            <a class="tile" href="/catalog?confectionerId=${c.id}">
                                <div class="tile-title">${c.name}</div>
                                <#if c.phone?? && c.phone?has_content>
                                    <div class="tile-sub">${c.phone}</div>
                                </#if>
                                <span class="price-tag">Каталог →</span>
                            </a>
                        </#if>
                    </#list>
                </div>
            </section>
        </#list>
    </div>
</main>
<#include "fragments/scripts.ftlh">
</body>
</html>
