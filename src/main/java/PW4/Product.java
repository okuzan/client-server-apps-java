package PW4;

public class Product {
    private Integer id;
    private String name;
    private double amount;
    private double price;

    public Product(String name) {
        this.name = name;
        this.price = -1;
    }
    public Product(final Integer id, String name, double amount, double price) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public  Product(String name, double amount, double price) {
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public void add(double amount) {
        this.amount += amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setId(int anInt) {
        this.id = anInt;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", price=" + price +
                '}';
    }

    public Integer getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.amount, amount) == 0 && Double.compare(product.price, price) == 0 && id.equals(product.id) && name.equals(product.name);
    }
}
