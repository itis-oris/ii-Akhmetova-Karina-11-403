function escapeHtml(s) { //защита от XSS экранирование спец символов
    if (s == null) return "";
    return String(s)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}

//предотвращение повторной отправки
document.addEventListener("submit", function (e) {
    const form = e.target; //сама форма
    if (!(form instanceof HTMLFormElement)) return; //проверяем что это форма
    if ((form.getAttribute("method") || "get").toLowerCase() !== "post") return; //смотрим чтобы метод был пост
    form.querySelectorAll('button[type="submit"], input[type="submit"]').forEach(function (btn) { //для каждой кнопки после нажатия ставим неактивна
        btn.disabled = true;
    });
});

//вызывается при загрузке страницы
//и при нажатии кнопки фильтрации
//получает торты через REST API
//фильтрует их на клиенте (поиск)
async function loadCakes() {
    const grid = document.getElementById("cakesGrid"); //карточка торта
    if (!grid) return;

    const confectionerId = grid.dataset.confectionerId;
    const qInput = document.getElementById("q");
    const q = ((qInput && qInput.value) ? qInput.value : "").trim().toLowerCase(); //поле ввода поиска
    const statusEl = document.getElementById("catalogSearchStatus"); //колво найденных тортов

    let cakes;
    //загружаем данные с сервера
    try {
        const url = confectionerId
            ? "/api/cakes?confectionerId=" + encodeURIComponent(confectionerId) //если есть айди конд
            : "/api/cakes"; //в ином случае все
            //разрешает отправку куки для аунт  получаем json
        const r = await fetch(url, { credentials: "same-origin", headers: { Accept: "application/json" } });
        if (!r.ok) {
            grid.innerHTML = "<p class=\"muted\">Ошибка загрузки</p>";
            return;
        }
        cakes = await r.json(); //json  в джава объект
    } catch {
        grid.innerHTML = "<p class=\"muted\">Нет сети</p>";
        return;
    }

    if (!Array.isArray(cakes)) { //является ли массивом а не ошибкой
        grid.innerHTML = "<p class=\"muted\">Неверный ответ</p>";
        return;
    }

    //если юзер что-то ввел в поиске
    if (q) {
        const filtered = []; //массив для подходящих тортов
        //проходим по всем тортам если есть подстрока берем
        for (let i = 0; i < cakes.length; i++) {
            const c = cakes[i] || {};
            const name = (c.name || "").toLowerCase();
            const cat = (c.category || "").toLowerCase();
            const sku = (c.sku || "").toLowerCase();
            if (name.includes(q) || cat.includes(q) || sku.includes(q)) {
                filtered.push(cakes[i]);
            }
        }
        cakes = filtered;
    }

    //выводим результат
    if (statusEl) {
        statusEl.textContent = q
            ? (cakes.length ? "По запросу «" + q + "» найдено: " + cakes.length + " шт." : "По запросу «" + q + "» ничего не найдено.")
            : "Показано: " + cakes.length;
    }

    //CSRF-токен для форм
    const csrf = window.csrfData
        ? '<input type="hidden" name="' + escapeHtml(window.csrfData.parameterName) + '" value="' + escapeHtml(window.csrfData.token) + '">'
        : "";

    //если тортов нет
    grid.innerHTML = "";
    if (cakes.length === 0) {
        grid.innerHTML = "<p class=\"muted\">Ничего не найдено</p>";
        return;
    }

    //рисуем карточки для каждого торта
    cakes.forEach(function (c) {
        //контейнер для одной карточки
        const card = document.createElement("div");
        card.className = "tile-cake";
        //формируем рейтинг
        const rating = c.averageRating != null
            ? "★ " + Number(c.averageRating).toFixed(1) + " / 5"
            : "<span class=\"muted\">Нет оценок</span>";
        //вес в граммах
        const weight = c.netWeightG != null ? c.netWeightG + " г" : "—";
        card.innerHTML =
            "<div class=\"tile-title\">" + escapeHtml(c.name || "") + "</div>" +
            "<div class=\"tile-sub\">Арт.: " + escapeHtml(c.sku || "—") + "</div>" +
            "<div class=\"tile-sub muted\">Категория: " + escapeHtml(c.category || "—") + " · Вес: " + escapeHtml(String(weight)) + "</div>" +
            "<div class=\"tile-sub\">" + rating + "</div>" +
            "<div class=\"tile-sub\"><span class=\"price-tag\">" + escapeHtml(String(c.price != null ? c.price : "—")) + " ₽</span></div>" +
            "<form method=\"post\" action=\"/orders\" style=\"margin-top:auto;\">" + csrf +
            "<input type=\"hidden\" name=\"cakeId\" value=\"" + c.id + "\">" +
            "<input type=\"hidden\" name=\"confectionerId\" value=\"" + confectionerId + "\">" +
            "<button class=\"btn\" type=\"submit\" style=\"width:100%;margin-top:12px;\">Заказать</button></form>";
        grid.appendChild(card); //добавляем карточку в общий контейнер
    });
}

//загружает с сервера данные о ближайшем магазине и его кондитерах
async function loadNearbyMap() {
    //наличие контейнера для карты
    if (!document.getElementById("mapNearestName")) return;

    const nameEl = document.getElementById("mapNearestName"); //назв ближ магазин
    const shopAddrEl = document.getElementById("mapShopAddress"); //адрес магазина
    const msg = document.getElementById("mapMessage"); //текст соо
    const teamUl = document.getElementById("mapBranchTeam"); //кондитеры ближ магаз
    const teamWrap = document.getElementById("mapBranchTeamWrap");

//временный текст загрузки
    if (nameEl) nameEl.textContent = "Загрузка…";
    if (msg) msg.textContent = "";

    //запрос к серверу без кэша получаем данные
    try {
        const r = await fetch("/api/maps/nearby", { credentials: "same-origin", cache: "no-store" });
        const data = await r.json();
        if (!r.ok) {
            if (msg) msg.textContent = data.error || "Ошибка";
            return;
        }

        const nearest = data.nearestShop;
        if (nearest) {
            if (nameEl) nameEl.textContent = nearest.shopName || "";
            if (shopAddrEl) shopAddrEl.textContent = nearest.shopAddress || "";
        } else {
            if (nameEl) nameEl.textContent = "";
            if (shopAddrEl) shopAddrEl.textContent = "";
        }

        if (msg) msg.textContent = data.message || "";

        //список кондитеров ближ магазина
        if (teamUl) {
            teamUl.innerHTML = "";
            (data.confectionersAtNearest || []).forEach(function (p) {
                if (!p || p.id == null) return;
                const li = document.createElement("li");
                li.className = "map-branch-team-item";
                const nameSpan = document.createElement("span");
                nameSpan.className = "map-branch-team-name";
                nameSpan.textContent = (p.name && String(p.name).trim()) || "Мастер";
                const btn = document.createElement("a");
                btn.className = "btn map-branch-catalog-btn";
                btn.href = "/catalog?confectionerId=" + encodeURIComponent(String(p.id));
                btn.textContent = "Каталог";
                li.appendChild(nameSpan);
                li.appendChild(btn);
                teamUl.appendChild(li);
            });
        }
        //показ/скрытие обертки списка
        if (teamWrap) {
            teamWrap.style.display = (data.confectionersAtNearest || []).length ? "block" : "none";
        }

        //установка ссылки на яндекс карты
        const link = data.yandexMapUrl;
        const a2 = document.getElementById("mapOpenTextLink");
        if (link && a2) a2.href = link;
    } catch {
        if (msg) msg.textContent = "Нет сети";
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const btn = document.getElementById("applyFilters");
    if (btn) {
        btn.addEventListener("click", loadCakes);
    }
    loadNearbyMap();
});
