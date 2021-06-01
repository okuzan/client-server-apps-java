package HW2.Storage;

import java.util.ArrayList;

public class ProductGroup {
    private ArrayList<Product> products;
    private String title;

    public ProductGroup(String title) {
        this.title = title;
        products = new ArrayList<>();
    }

    public void addGoods(Product g) {
        for (Product product : products) {
            if (product.getName().equals(g.getName())) {
                product.setAmount(product.getAmount() + g.getAmount());
                return;
            }
        }
        //not present for now
        products.add(g);
    }

    public boolean isPresent(String name) {
        for (Product p : products)
            if (p.getName().equals(name))
                return true;
        return false;
    }

    public boolean ifPresentUpdate(String name, double price) {
        for (Product product : products)
            if (product.getName().equals(name)) {
                product.setPrice(price);
                return true;
            }
        return false;
    }

    public boolean ifPresentUpdate(String name, int amount) {
        for (Product product : products)
            if (product.getName().equals(name)) {
                if (product.getAmount() != -1)
                    if (product.getAmount() + amount < 0)
                        throw new IllegalArgumentException("Not enough products in the warehouse!");
                    else
                        product.setAmount(product.getAmount() + amount);

                else
                    product.setAmount(amount);
                return true;
            }
        return false;
    }

    public void deleteGoods(Product g) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getName().equals(g.getName())) {
                if (products.get(i).getAmount() < g.getAmount()) {
                    throw new IllegalArgumentException();
                    //list.remove(i);
                }
                products.get(i).setAmount(products.get(i).getAmount() - g.getAmount());
                return;
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer ifPresentGetQ(String name) {
        for (Product p : products)
            if (p.getName().equals(name))
                return p.getAmount();
        return null;
    }
}
