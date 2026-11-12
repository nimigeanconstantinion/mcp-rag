package mycode.onlineshopspring.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mycode.onlineshopspring.auth.customer.Customer;
import mycode.onlineshopspring.auth.customer.CustomerRepository;
import mycode.onlineshopspring.auth.permission.Permission;
import mycode.onlineshopspring.auth.permission.PermissionRepository;
import mycode.onlineshopspring.auth.permission.Permissions;
import mycode.onlineshopspring.auth.user.User;
import mycode.onlineshopspring.orderDetails.models.OrderDetails;
import mycode.onlineshopspring.orders.models.Orders;
import mycode.onlineshopspring.products.models.Products;
import mycode.onlineshopspring.products.repository.ProductsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DemoShopSeeder {

    private static final String CUSTOMER_PASSWORD = "customer1234";

    private final ProductsRepository productsRepository;
    private final CustomerRepository customerRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    private record Line(Products product, int quantity) {
    }

    @Transactional
    public void seed() {
        if (productsRepository.count() > 0) return;

        Products headphones = product("AUD-001", "Căști wireless over-ear Aria 500", 349, 260,
                "Căști over-ear cu anulare activă a zgomotului, Bluetooth 5.3, autonomie 40 de ore", "Audio", 12);
        Products earbuds = product("AUD-002", "Căști in-ear Aria Buds", 199, 50,
                "Căști in-ear true wireless, cu carcasă de încărcare și trei perechi de dopuri din silicon", "Audio", 25);
        Products laptop = product("LAP-001", "Laptop Nova 14", 3499, 1400,
                "Laptop de 14 inch, 16 GB RAM, SSD 512 GB, produs nou, sigilat", "Laptopuri", 4);
        Products refurbishedLaptop = product("LAP-002", "Laptop Nova 14 resigilat", 2799, 1400,
                "Laptop de 14 inch, 16 GB RAM, SSD 512 GB, produs resigilat, verificat în service", "Resigilate", 2);
        Products mouse = product("ACC-001", "Mouse wireless Glide", 89, 90,
                "Mouse wireless cu receptor USB și baterie AA inclusă", "Accesorii", 40);
        Products keyboard = product("ACC-002", "Tastatură mecanică Tactile 87", 279, 800,
                "Tastatură mecanică compactă, switch-uri roșii, iluminare albă", "Accesorii", 0);
        Products phone = product("PHN-001", "Telefon Lumen X", 2199, 190,
                "Telefon de 6,5 inch, 256 GB, dual SIM, produs nou, sigilat", "Telefoane", 7);
        Products antivirus = product("SFT-001", "Licență antivirus Shield, 1 an", 129, 0,
                "Licență digitală pentru un dispozitiv, codul se trimite pe e-mail", "Software", 100);
        productsRepository.saveAll(List.of(headphones, earbuds, laptop, refurbishedLaptop, mouse, keyboard, phone, antivirus));

        Customer ana = customer("ana.pop@example.com", "Ana Pop", "Str. Memorandumului 12, Cluj-Napoca", "0721000101");
        order(ana, 5, "DELIVERED", new Line(headphones, 1));
        order(ana, 45, "DELIVERED", new Line(mouse, 1));
        order(ana, 29, "DELIVERED", new Line(keyboard, 1));

        Customer mihai = customer("mihai.ionescu@example.com", "Mihai Ionescu", "Bd. Unirii 45, București", "0721000102");
        order(mihai, 10, "DELIVERED", new Line(earbuds, 1));
        order(mihai, 2, "SHIPPED", new Line(phone, 1));

        Customer elena = customer("elena.radu@example.com", "Elena Radu", "Str. Lăpușneanu 7, Iași", "0721000103");
        order(elena, 20, "DELIVERED", new Line(refurbishedLaptop, 1));
        order(elena, 1, "PENDING", new Line(mouse, 1), new Line(keyboard, 1));

        Customer dan = customer("dan.stoica@example.com", "Dan Stoica", "Str. Republicii 3, Brașov", "0721000104");
        order(dan, 8, "DELIVERED", new Line(antivirus, 1));
        order(dan, 60, "CANCELLED", new Line(laptop, 1));
        order(dan, 12, "DELIVERED", new Line(laptop, 1), new Line(mouse, 1));

        customerRepository.saveAll(List.of(ana, mihai, elena, dan));
        log.info("Seeded demo shop: 8 products, 4 customers, 10 orders (customer password: {})", CUSTOMER_PASSWORD);
    }

    private Products product(String sku, String name, int price, int weight, String description, String category, int stock) {
        Products product = new Products();
        product.setSku(sku);
        product.setName(name);
        product.setPrice(price);
        product.setWeight(weight);
        product.setDescriptions(description);
        product.setCategory(category);
        product.setCreateDate(new Date());
        product.setStock(stock);
        return product;
    }

    private Customer customer(String email, String fullName, String address, String phone) {
        Set<Permission> permissions = new HashSet<>(permissionRepository.findByNameIn(
                new HashSet<>(Arrays.asList(Permissions.customerDefaults()))));

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(CUSTOMER_PASSWORD))
                .enabled(true)
                .permissions(permissions)
                .build();

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFullName(fullName);
        customer.setBillingAddress(address);
        customer.setDefaultShippingAddress(address);
        customer.setCountry("RO");
        customer.setPhone(phone);
        return customer;
    }

    private void order(Customer customer, int daysAgo, String status, Line... lines) {
        Orders order = new Orders();
        order.setShippingAdress(customer.getDefaultShippingAddress());
        order.setOrderAdress(customer.getBillingAddress());
        order.setOrderEmail(customer.getUser().getEmail());
        order.setOrder_date(LocalDate.now().minusDays(daysAgo));
        order.setOrderStatus(status);

        int amount = 0;
        for (Line line : lines) {
            OrderDetails detail = new OrderDetails();
            detail.setProduct(line.product());
            detail.setSku(line.product().getSku());
            detail.setPrice(line.product().getPrice());
            detail.setQuantity(line.quantity());
            order.addOrderDetails(detail);
            amount += line.product().getPrice() * line.quantity();
        }
        order.setAmmount(amount);
        customer.addOrder(order);
    }
}
