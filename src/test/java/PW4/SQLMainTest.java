package PW4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SQLMainTest {

    @Test
    void shouldInsertProduct() {
        SQLMain sqlMain = new SQLMain();
        sqlMain.initialization(".");
        int size = sqlMain.getAllProducts().size();
        Product product = new Product("test", 1.1, 1.4);
        sqlMain.insertProductData(product);

        System.out.println(sqlMain.getAllProducts());

        assertThat(product.getId()).isNotNull();
        assertThat(sqlMain.getAllProducts().size() - 1).isEqualTo(size);
        assertThat(sqlMain.readProduct(1)).isEqualTo(new Product(1, "test", 1.1, 1.4));
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    void shouldSelectByFilterParametrized(ProductFilter filter, List<Product> expectedProducts) {
        SQLMain sqlMain = new SQLMain();

        sqlMain.initialization(".");
        sqlMain.insertProductData(
                new Product("name1", 1, 10)
        );
        sqlMain.insertProductData(
                new Product("name2", 2, 9)
        );
        sqlMain.insertProductData(
                new Product("name3", 3, 8)
        );
        sqlMain.insertProductData(
                new Product("name4", 4, 7)
        );

        assertThat(sqlMain.getByCriteria(filter)).containsExactlyElementsOf(expectedProducts);
    }

    private static Stream<Arguments> dataProvider() {
        return Stream.of(
                Arguments.of(
                        new ProductFilter(null, 2., 3., null, null),
                        List.of(
                                new Product(2, "name2", 2, 9),
                                new Product(3, "name3", 3, 8)
                        ),
                        new ProductFilter(null, null, null, 7., 9.),
                        List.of(
                                new Product(2, "name2", 2, 9),
                                new Product(3, "name3", 3, 8),
                                new Product(4, "name4", 4, 7)
                        ),
                        new ProductFilter(null, null, null, null, null),
                        List.of(
                                new Product(1, "name1", 1, 10),
                                new Product(2, "name2", 2, 9),
                                new Product(3, "name3", 3, 8),
                                new Product(4, "name4", 4, 7)
                        ),
                        new ProductFilter("3", null, null, null, null),
                        List.of(
                                new Product(3, "name3", 3, 8)
                        ),
                        new ProductFilter("", null, 3., 8., null),
                        List.of(
                                new Product(3, "name3", 3, 8)
                        )
                )
        );
    }

    @Test
    void shouldSelectByFilter() {
        SQLMain sqlMain = new SQLMain();
        sqlMain.initialization(".");
        Product product1 = sqlMain.insertProductData(new Product("prod1", 1.1, 1.1));
        Product product2 = sqlMain.insertProductData(new Product("prod2", 2.1, 2.1));
        ProductFilter filter = new ProductFilter();
        filter.setMinPrice(1.5);
        assertThat(sqlMain.getByCriteria(filter)).contains(product2);
    }

    @Test
    void shouldReadProduct() {
        SQLMain sqlMain = new SQLMain();
        sqlMain.initialization(".");

        Product product1 = sqlMain.insertProductData(new Product("prod1", 1.1, 1.1));
        Product product2 = sqlMain.insertProductData(new Product("prod2", 2.1, 2.1));
        Product product3 = sqlMain.insertProductData(new Product("prod3", 3.1, 3.1));

        System.out.println(sqlMain.getAllProducts());

        assertThat(sqlMain.readProduct(1)).isEqualTo(product1);
        assertThat(sqlMain.readProduct(2)).isEqualTo(product2);
        assertThat(sqlMain.readProduct(3)).isEqualTo(product3);
    }

    @Test
    void shouldDeleteProduct() {
        SQLMain sqlMain = new SQLMain();
        sqlMain.initialization(".");
        Product product1 = sqlMain.insertProductData(new Product("prod1", 1.1, 1.1));
        Product product2 = sqlMain.insertProductData(new Product("prod2", 2.1, 2.1));
        Product product3 = sqlMain.insertProductData(new Product("prod3", 3.1, 3.1));

        System.out.println(sqlMain.getAllProducts());
        sqlMain.deleteProduct(2);
        assertThat(sqlMain.getAllProducts()).containsExactly(product1, product3);
        sqlMain.deleteProduct(1);
        assertThat(sqlMain.getAllProducts()).containsExactly(product3);
        sqlMain.deleteProduct(3);
        assertThat(sqlMain.getAllProducts()).isEmpty();
    }

    @Test
    void shouldUpdateProduct() {
        SQLMain sqlMain = new SQLMain();
        sqlMain.initialization("*");

        sqlMain.insertProductData(new Product("prod1", 1.1, 1.1));
        sqlMain.insertProductData(new Product("prod2", 2.1, 2.1));
        sqlMain.insertProductData(new Product("prod3", 3.1, 3.1));
        sqlMain.insertProductData(new Product("prod4", 4.1, 4.1));

        Product product1upd = (new Product(1, "prod1", 1.1, 77.0));
        Product product2upd = (new Product(2, "prod2plus", 2.1, 2.1));
        Product product3upd = (new Product(3, "prod3", 99.0, 3.1));
        Product product4upd = (new Product(4, "prod4plus", 101.0, 101.0));

        System.out.println(sqlMain.getAllProducts());

        sqlMain.updateProduct(1, "", null, 77.0);
        sqlMain.updateProduct(2, "prod2plus", null, null);
        sqlMain.updateProduct(3, "", 99.0, null);
        sqlMain.updateProduct(4, "prod4plus", 101.0, 101.0);

        assertThat(sqlMain.readProduct(1)).isEqualTo(product1upd);
        assertThat(sqlMain.readProduct(2)).isEqualTo(product2upd);
        assertThat(sqlMain.readProduct(3)).isEqualTo(product3upd);
        assertThat(sqlMain.readProduct(4)).isEqualTo(product4upd);
    }


}