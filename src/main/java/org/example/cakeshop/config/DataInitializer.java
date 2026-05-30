package org.example.cakeshop.config;

import org.example.cakeshop.entity.Cake;
import org.example.cakeshop.entity.Confectioner;
import org.example.cakeshop.entity.Customer;
import org.example.cakeshop.entity.Review;
import org.example.cakeshop.entity.Role;
import org.example.cakeshop.entity.Shop;
import org.example.cakeshop.repository.CakeRepository;
import org.example.cakeshop.repository.ConfectionerRepository;
import org.example.cakeshop.repository.CustomerRepository;
import org.example.cakeshop.repository.OrderRepository;
import org.example.cakeshop.repository.ReviewRepository;
import org.example.cakeshop.repository.RoleRepository;
import org.example.cakeshop.repository.ShopRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    // 3 точки сети в Казани
    private static final Object[][] DEMO_SHOPS = {
            {"Центр — ул. Баумана", "Казань, ул. Баумана, 19", 55.79125d, 49.11420d},
            {"Центр — Петербургская", "Казань, ул. Петербургская, 50", 55.78143d, 49.13282d},
            {"Север — Декабристов", "Казань, ул. Декабристов, 85", 55.81855d, 49.09042d}
    };

    /** Имя, телефон, индекс точки */
    private static final Object[][] DEMO_CONFECTIONERS = {
            {"Динар", "+7 999 000-00-01", 0},
            {"Алия", "+7 999 000-00-02", 0},
            {"Самира", "+7 999 000-00-03", 1},
            {"Карим", "+7 999 000-00-04", 1},
            {"Руслан", "+7 999 000-00-05", 2}
    };

    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final ConfectionerRepository confectionerRepository;
    private final CakeRepository cakeRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    //передаются все репозитории и шифрование пароля
    public DataInitializer(RoleRepository roleRepository,
                         CustomerRepository customerRepository,
                         ShopRepository shopRepository,
                         ConfectionerRepository confectionerRepository,
                         CakeRepository cakeRepository,
                         ReviewRepository reviewRepository,
                         OrderRepository orderRepository,
                         PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.shopRepository = shopRepository;
        this.confectionerRepository = confectionerRepository;
        this.cakeRepository = cakeRepository;
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role userRole = ensureRole("ROLE_USER"); //получ или созд роль юзер
        Role adminRole = ensureRole("ROLE_ADMIN");//или админ
        ensureAdmin(userRole, adminRole); //созд или получ админа с логином и паролем админа и даем ему обе роли
        List<Shop> shops = ensureShops(); //созд или получ 3 магазина
        ensureConfectioners(shops); //созд или получ кондитеров привязывая к магазинам
        if (cakeRepository.count() == 0) {
            seedCakesAndReview();
        }
    }

    //находим роль по имени если нет создаем новую и возвр
    private Role ensureRole(String name) {
        return roleRepository.findByName(name).orElseGet(() -> roleRepository.save(new Role(name)));
    }

    //ищем админа по имени админ или по admin@gmail.com
    private void ensureAdmin(Role userRole, Role adminRole) {
        Optional<Customer> adminOpt = customerRepository.findByUsernameIgnoreCase("admin")
                .or(() -> customerRepository.findByEmailIgnoreCase("admin@gmail.com"));
        Customer admin = adminOpt.orElseGet(Customer::new); //если найден использ его если нет создаем нового
        admin.setUsername("admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setRoles(Set.of(userRole, adminRole));
        customerRepository.save(admin); //сохраняем/обновляем админа
    }

    //базовые 3 магазина
    private List<Shop> ensureShops() {
        List<Shop> existing = new ArrayList<>(shopRepository.findAllByOrderByIdAsc());
        List<Shop> result = new ArrayList<>();
        for (int i = 0; i < DEMO_SHOPS.length; i++) {
            Shop s = i < existing.size() ? existing.get(i) : new Shop();
            Object[] row = DEMO_SHOPS[i];
            s.setBranchTitle((String) row[0]);
            s.setAddress((String) row[1]);
            s.setLatitude((Double) row[2]);
            s.setLongitude((Double) row[3]);
            result.add(shopRepository.save(s));
        }
        return result;
    }

    private void ensureConfectioners(List<Shop> shops) {
        if (shops.size() < DEMO_SHOPS.length) {
            return;
        }
        // создаем/обновляем кондитеров
        List<Confectioner> existing = new ArrayList<>(confectionerRepository.findAllByOrderByIdAsc());
        for (int i = 0; i < DEMO_CONFECTIONERS.length; i++) {
            Confectioner c = i < existing.size() ? existing.get(i) : new Confectioner();
            Object[] row = DEMO_CONFECTIONERS[i];
            c.setName((String) row[0]);
            c.setPhone((String) row[1]);
            int shopIdx = (Integer) row[2];
            c.setShop(shops.get(shopIdx));
            confectionerRepository.save(c);
        }
        //удаляем лишних
        for (int i = DEMO_CONFECTIONERS.length; i < existing.size(); i++) {
            Confectioner extra = existing.get(i);
            if (orderRepository.countByConfectionerId(extra.getId()) > 0) { //сколько заказов у кондитера
                continue;
            }
            cakeRepository.findByConfectioners_Id(extra.getId()).forEach(cake -> {
                cake.getConfectioners().remove(extra); //перед удалением отвязываем от тортов
                cakeRepository.save(cake);
            });
            confectionerRepository.delete(extra);
        }
    }

    private void seedCakesAndReview() {
        //создаем два торта
        Cake cake1 = new Cake();
        cake1.setName("Медовик классический");
        cake1.setSku("CAKE-042");
        cake1.setCategory("Бисквитные");
        cake1.setNetWeightKg(1.5);
        cake1.setPrice(3200.0);

        Cake cake2 = new Cake();
        cake2.setName("Три шоколада (муссовый)");
        cake2.setSku("CAKE-043");
        cake2.setCategory("Муссовые");
        cake2.setNetWeightKg(1.0);
        cake2.setPrice(2900.0);

        cakeRepository.saveAll(List.of(cake1, cake2));

        //привязываем первых двух кондитеров к тортам
        List<Confectioner> list = confectionerRepository.findAllByOrderByIdAsc();
        if (list.size() >= 2) {
            cake1.getConfectioners().add(list.get(0));
            cake2.getConfectioners().add(list.get(1));
            cakeRepository.saveAll(List.of(cake1, cake2));
        }

        //отзыв от админа
        customerRepository.findByEmailIgnoreCase("admin@gmail.com").ifPresent(reviewer -> {
            Review r = new Review();
            r.setCustomer(reviewer);
            r.setCake(cake1);
            r.setRating(5);
            r.setComment("Красивый декор и воздушные коржи.");
            reviewRepository.save(r);
        });
    }
}
