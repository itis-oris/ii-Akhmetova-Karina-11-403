<#-- Каркас: head / header / main / scripts -->
<#macro shell title="">
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <#include "styles.ftlh">
</head>
<body>
    <#include "header.ftlh">
    <main class="main">
        <#nested>
    </main>
    <#include "scripts.ftlh">
</body>
</html>
</#macro>
