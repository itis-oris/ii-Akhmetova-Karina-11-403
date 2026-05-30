<header class="header">
    <div class="header-inner">
        <a class="brand" href="/">
            <span class="brand-mark" aria-hidden="true">🎂</span>
            CakeShop
        </a>

        <nav class="nav">
            <a href="/">Главная</a>
            <a href="/catalog">Каталог</a>
            <#if isAuthenticated?? && isAuthenticated>
                <a href="/profile">Профиль</a>
            </#if>
            <#if isAdmin?? && isAdmin>
                <a href="/admin/cakes">Админ</a>
            </#if>
        </nav>

        <div class="auth">
            <#if isAuthenticated?? && isAuthenticated>
                <form method="post" action="/logout" style="display:inline;margin:0;">
                    <input type="hidden" name="${csrfParameterName!''}" value="${csrfToken!''}">
                    <button class="btn" type="submit">Выйти</button>
                </form>
            <#else>
                <a class="btn btn-ghost" href="/register">Регистрация</a>
                <a class="btn" href="/login">Войти</a>
            </#if>
        </div>
    </div>
</header>
