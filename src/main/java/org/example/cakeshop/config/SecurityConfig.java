package org.example.cakeshop.config;

import org.example.cakeshop.security.CustomerUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //сравнивает сырой пароль и хэшем в бд
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //принимает логин и пароль из формы
    //пользователя через UserDetailsService по логину
    //проверять пароль с помощью PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomerUserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                .authenticationProvider(authenticationProvider) //кто отвечает за проверку
                .authorizeHttpRequests(auth -> auth //правила доступа
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**",
                                "/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll() //видны всем
                        .requestMatchers(HttpMethod.GET, "/catalog").permitAll() //можно только посмотреть
                        .requestMatchers(HttpMethod.GET, "/api/cakes", "/api/cakes/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN") //в админку у кого роль админ
                        .requestMatchers("/api/**").authenticated() //апи запросы залогинен но не важна роль
                        .anyRequest().authenticated() //остальные залогинен
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/profile", true)
                        .failureUrl("/login?error=true")
                        .permitAll() //доступна всем
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true) //удалить данные сессии
                        .clearAuthentication(true) //забыть что юзер был залогинен
                )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

        return http.build();
    }
}

