package PW4;

public class ProductFilter {
    private String name;
    private Double minPrice;
    private Double maxPrice;
    private Double minAmount;
    private Double maxAmount;

    public ProductFilter() {
    }

    public ProductFilter(String name, Double minAmount, Double maxAmount, Double minPrice, Double maxPrice) {
        this.name = name;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }
}
