package org.example.cakeshop.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice //применяет ко всем @Controller
public class WebModelAttributesAdvice {
    @ModelAttribute("csrfParameterName")
    public String csrfParameterName(HttpServletRequest request) { //передаст тек запрос автомат
        CsrfToken token = extractToken(request);
        return token != null ? token.getParameterName() : "_csrf";
    }

    //само значение
    @ModelAttribute("csrfToken")
    public String csrfToken(HttpServletRequest request) {
        CsrfToken token = extractToken(request);
        return token != null ? token.getToken() : "";
    }

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated() {
        //тек юзер
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //есть ли инфа и авторизован и не анонимный юзер
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        //тек юзер
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //если нет инфы или нет списка ролей
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        //есть ли среди ролей админ
        return auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    //достает CSRF токен из атрибутов запроса.
    //токен может лежать под разными ключами
    //1)по полному имени класса CsrfToken.class.getName()
    //2)по стандартному имени _csrf
    private CsrfToken extractToken(HttpServletRequest request) {
        Object byClassName = request.getAttribute(CsrfToken.class.getName());
        if (byClassName instanceof CsrfToken token) {
            return token;
        }
        Object byDefaultName = request.getAttribute("_csrf");
        if (byDefaultName instanceof CsrfToken token) {
            return token;
        }
        return null;
    }
}

