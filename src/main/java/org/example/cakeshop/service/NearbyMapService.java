package org.example.cakeshop.service;

import org.example.cakeshop.dto.GeoPoint;
import org.example.cakeshop.dto.NearbyMapResponse;
import org.example.cakeshop.dto.NearestShopDto;
import org.example.cakeshop.dto.ProfileConfectionerDto;
import org.example.cakeshop.entity.Customer;
import org.example.cakeshop.entity.Shop;
import org.example.cakeshop.exception.NotFoundException;
import org.example.cakeshop.repository.ConfectionerRepository;
import org.example.cakeshop.repository.CustomerRepository;
import org.example.cakeshop.repository.ShopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Service
public class NearbyMapService {

    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final ConfectionerRepository confectionerRepository;
    private final AddressLocationService addressLocationService;

    public NearbyMapService(CustomerRepository customerRepository,
                            ShopRepository shopRepository,
                            ConfectionerRepository confectionerRepository,
                            AddressLocationService addressLocationService) {
        this.customerRepository = customerRepository;
        this.shopRepository = shopRepository;
        this.confectionerRepository = confectionerRepository;
        this.addressLocationService = addressLocationService;
    }

    @Transactional
    public NearbyMapResponse buildForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        String address = customer.getAddress();
        Optional<GeoPoint> userPoint = resolveUserPoint(customer, address);

        List<Shop> shops = shopRepository.findAllByOrderByIdAsc().stream()
                .filter(s -> s.getLatitude() != null && s.getLongitude() != null)
                .toList();

        Shop nearest = null;
        NearestShopDto nearestShop = null;
        List<ProfileConfectionerDto> team = List.of();
        Double nearestKm = null;

        if (!shops.isEmpty() && userPoint.isPresent()) {
            GeoPoint user = userPoint.get();
            //находит ближ магазин
            nearest = shops.stream()
                    .min(Comparator.comparingDouble(s -> distanceKm(user, s)))
                    .orElse(null);

            if (nearest != null) {
                nearestKm = distanceKm(user, nearest);
                double kmRounded = Math.round(nearestKm * 10.0) / 10.0;
                nearestShop = new NearestShopDto(
                        nearest.getId(),
                        nearest.getBranchTitle(),
                        nearest.getAddress(),
                        kmRounded);

                team = confectionerRepository.findByShop_IdOrderByNameAsc(nearest.getId()).stream()
                        .map(c -> new ProfileConfectionerDto(c.getId(), c.getName()))
                        .toList();
            }
        }

        String message = message(address, userPoint.isPresent(), nearestKm);
        String yandexUrl = yandexLink(nearest, userPoint.orElse(null));

        return new NearbyMapResponse(message, nearestShop, team, yandexUrl);
    }

    private Optional<GeoPoint> resolveUserPoint(Customer customer, String address) {
        if (address != null && !address.isBlank()) {
            Optional<GeoPoint> geocoded = addressLocationService.geocode(address);
            geocoded.ifPresent(p -> persistCoordsIfChanged(customer, p));
            return geocoded;
        }
        if (customer.getLatitude() != null && customer.getLongitude() != null) {
            return Optional.of(new GeoPoint(customer.getLatitude(), customer.getLongitude()));
        }
        return Optional.empty();
    }

    private void persistCoordsIfChanged(Customer customer, GeoPoint p) {
        if (!Objects.equals(customer.getLatitude(), p.getLatitude())
                || !Objects.equals(customer.getLongitude(), p.getLongitude())) {
            customer.setLatitude(p.getLatitude());
            customer.setLongitude(p.getLongitude());
            customerRepository.save(customer);
        }
    }

    private static String message(String address, boolean hasPoint, Double km) {
        if (!hasPoint) {
            if (address == null || address.isBlank()) {
                return "Укажите адрес в Казани, например: Казань, ул. Баумана, 19";
            }
            return "Не удалось определить координаты по адресу. Проверьте формулировку и сохраните профиль.";
        }
        if (km == null) {
            return "";
        }
        return String.format(Locale.forLanguageTag("ru-RU"), "До ближайшей точки: %.1f км.", km);
    }

    private static double distanceKm(GeoPoint user, Shop shop) {
        double r = 6371.0;
        double dLat = Math.toRadians(shop.getLatitude() - user.getLatitude());
        double dLon = Math.toRadians(shop.getLongitude() - user.getLongitude());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(user.getLatitude())) * Math.cos(Math.toRadians(shop.getLatitude()))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private static String yandexLink(Shop nearest, GeoPoint user) {
        if (nearest != null) {
            return "https://yandex.ru/maps/?ll=" + nearest.getLongitude() + "," + nearest.getLatitude()
                    + "&z=15&l=map&pt=" + nearest.getLongitude() + "," + nearest.getLatitude() + ",pm2rdm";
        }
        if (user != null) {
            return "https://yandex.ru/maps/?ll=" + user.getLongitude() + "," + user.getLatitude()
                    + "&z=12&l=map&pt=" + user.getLongitude() + "," + user.getLatitude() + ",pm2grm";
        }
        return null;
    }
}
