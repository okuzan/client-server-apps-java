package HW2.Storage;

public class Product {
    private String name;
    private int amount;
    private double price;

    public Product(String name) {
        this.name = name;
        this.price = -1;
        this.amount = -1;
    }

    public Product(String name, int amount, double price) {
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    public void add(int amount) {
        this.amount += amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
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
}
