package PW4;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class SQLMain {
    private Connection con;

    public void initialization(String name) {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite::memory:");
//            con = DriverManager.getConnection("jdbc:sqlite:" + name);
            PreparedStatement st = con.prepareStatement(
                    "create table if not exists 'warehouse' (" +
                            "'id' INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " 'name' text," +
                            " 'amount' double," +
                            " 'price' double" +
                            ");");
            int result = st.executeUpdate();
        } catch (ClassNotFoundException e) {
            System.out.println("Не знайшли драйвер JDBC");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            System.out.println("Не вірний SQL запит");
            e.printStackTrace();
        }
    }

    public Product insertProductData(Product product) {
        try (PreparedStatement statement = con.prepareStatement("INSERT INTO warehouse(name, amount, price) VALUES (?,?,?)")) {
            System.out.println("AMOUNT:");
            System.out.println((product.getAmount()));
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getAmount());
            statement.setDouble(3, product.getPrice());

            statement.executeUpdate();
            statement.close();
            ResultSet resultSet = statement.getGeneratedKeys();
            int id = resultSet.getInt("last_insert_rowid()");
            product.setId(id);

            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert product", e);
        }
    }

    public List<Product> getAllProducts() {
        try (Statement st = con.createStatement();
             ResultSet res = st.executeQuery("SELECT * FROM warehouse");
        ) {
            List<Product> products = new ArrayList<>();
            while (res.next()) {
                products.add(new Product(res.getInt("id"), res.getString("name"), res.getDouble("price"), res.getDouble("amount")));
            }
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Can't select all products!", e);
        }
    }

    public Product readProduct(int id) {
        try {
            String sql = "SELECT * FROM warehouse WHERE id = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            statement.execute();
            ResultSet res = statement.getResultSet();
            return new Product(
                    res.getInt("id"),
                    res.getString("name"),
                    res.getDouble("amount"),
                    res.getDouble("price")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Can't select all products!", e);
        }
    }

    public void deleteProduct(int id) {
        try {
            String sql = "DELETE FROM warehouse WHERE id = ?";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't select all products!", e);
        }
    }

    public void updateProduct(int id, String name, Double amount, Double price) {
        try {
            if (name.equals("") && price == null && amount == null)
                throw new RuntimeException("No new data in update!");

            StringBuilder sb = new StringBuilder();
            sb.append("UPDATE warehouse SET ");
            List<Object> values = new ArrayList<>();
            if (!name.equals("")) {
                sb.append(" name = ? ,");
                values.add(name);
            }
            if (price != null) {
                sb.append(" price = ? ,");
                values.add(price);
            }
            if (amount != null) {
                sb.append(" amount = ? ,");
                values.add(amount);
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(" WHERE id = ?");
            System.out.println(sb);

            PreparedStatement statement = con.prepareStatement(sb.toString());
            statement.setInt(1, id);
            int i = 1;
            for (Object obj : values) {
                System.out.println("name: " + obj.getClass().getName());
                switch (obj.getClass().getName()) {
                    case "java.lang.String":
                        System.out.printf("i: %s, string\n", i);
                        statement.setString(i++, (String) obj);
                        break;
                    case "java.lang.Double":
                        System.out.printf("i: %s, double\n", i);
                        statement.setDouble(i++, (Double) obj);
                        break;
                }
            }
            statement.setInt(i, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Can't select all products!", e);
        }
    }


    public List<Product> getByCriteria(ProductFilter filter) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM warehouse WHERE 1=1 ");

        if (filter.getName() != null) sb.append(" AND name like '%").append(filter.getName()).append("%'");
        if (filter.getMinPrice() != null) sb.append(" AND price >= ").append(filter.getMinPrice());
        if (filter.getMaxPrice() != null) sb.append(" AND price <= ").append(filter.getMaxPrice());
        if (filter.getMinAmount() != null) sb.append(" AND amount >= ").append(filter.getMinAmount());
        if (filter.getMaxAmount() != null) sb.append(" AND amount <= ").append(filter.getMaxAmount());

        try (Statement st = con.createStatement(); ResultSet res = st.executeQuery(sb.toString())) {
            List<Product> products = new ArrayList<>();
            while (res.next()) products.add(
                    new Product(res.getInt("id"),
                            res.getString("name"),
                            res.getDouble("amount"),
                            res.getDouble("price")
                    )
            );
            return products;
        } catch (SQLException e) {
            throw new RuntimeException("Can't select all products!", e);
        }
    }

    public static void main(String[] args) {
        SQLMain sqlMain = new SQLMain();
        sqlMain.initialization("Warehouse");
//        Product product1 = sqlMain.insertProductData(new Product("test", 13.2, 55.1));
//        ProductFilter filter = new ProductFilter();
//        filter.setName("bread");
//        filter.setMinPrice(15.0);
//        System.out.println(sqlMain.getByCriteria(filter));
//        System.out.println(sqlMain.getAllProducts());
//        sqlMain.deleteProduct(11);
//        sqlMain.deleteProduct(5);
//        System.out.println(sqlMain.getAllProducts());
//        System.out.println(sqlMain.readProduct(1));
//        System.out.println(sqlMain.readProduct(2));
        sqlMain.updateProduct(12, "goodname", null, 100.1);
    }
}