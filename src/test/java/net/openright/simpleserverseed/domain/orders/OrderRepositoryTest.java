package net.openright.simpleserverseed.domain.orders;

import static org.assertj.core.api.Assertions.assertThat;

import net.openright.infrastructure.db.PgsqlDatabase;
import net.openright.infrastructure.test.SampleData;
import net.openright.simpleserverseed.application.SeedAppConfig;
import net.openright.simpleserverseed.application.SimpleseedTestConfig;
import net.openright.simpleserverseed.domain.products.Product;
import net.openright.simpleserverseed.domain.products.ProductRepository;
import net.openright.simpleserverseed.domain.products.ProductRepositoryTest;

import org.junit.Before;
import org.junit.Test;

public class OrderRepositoryTest {

    private SeedAppConfig config = new SimpleseedTestConfig();
    private PgsqlDatabase database = new PgsqlDatabase(config.createDataSource());
    private OrdersRepository repository = new OrdersRepository(database);
    private ProductRepository productRepository = new ProductRepository(database);
    private Product product = ProductRepositoryTest.sampleProduct();

    @Before
    public void saveProduct() {
        productRepository.insert(product);
    }

    @Test
    public void shouldRetrieveSavedOrdersWithoutOrderLines() throws Exception {
        Order order = sampleOrder();
        repository.insert(order);
        assertThat(repository.list()).contains(order);

        assertThat(repository.retrieve(order.getId()))
            .isEqualToComparingFieldByField(order);
    }

    @Test
    public void shouldListOrdersWithoutOrderLines() throws Exception {
        Order order = sampleOrder();
        order.addOrderLine(product.getId(), 2);
        repository.insert(order);

        Order savedOrder =  repository.list().stream()
                .filter(o -> o.getId() == order.getId())
                .findFirst().get();
        assertThat(savedOrder.getOrderLines()).isEmpty();
    }

    @Test
    public void shouldIncludeProductWhenRetrievingOrder() throws Exception {
        Order order = sampleOrder();
        order.addOrderLine(product.getId(), 2);
        repository.insert(order);

        Order savedOrder = repository.retrieve(order.getId());
        assertThat(savedOrder.getOrderLines().get(0).getProduct().get())
            .isEqualTo(product);
        assertThat(savedOrder.getOrderLines().get(0).getAmount())
            .isEqualTo(2);
    }

    @Test
    public void shouldRetrieveSavedOrdersWithOrderLines() throws Exception {
        Product product1 = ProductRepositoryTest.sampleProduct();
        Product product2 = ProductRepositoryTest.sampleProduct();
        productRepository.insert(product1);
        productRepository.insert(product2);

        Order order = sampleOrder();

        order.addOrderLine(product1.getId(), 10);
        order.addOrderLine(product2.getId(), 100);

        repository.insert(order);

        Order savedOrder = repository.retrieve(order.getId());
        assertThat(savedOrder).isEqualToComparingFieldByField(order);
        assertThat(savedOrder.getTotalAmount())
            .isEqualTo(10 * product1.getPrice() + 100 * product2.getPrice());
    }

    public static Order sampleOrder() {
        return new Order(SampleData.sampleString(4));
    }

}
