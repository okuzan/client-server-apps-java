package HW2.Storage;

import java.util.concurrent.LinkedBlockingQueue;

public class Warehouse {

    //check
    private final LinkedBlockingQueue<ProductGroup> groups;

    public Warehouse() {
        groups = new LinkedBlockingQueue<>();
    }

    public Warehouse(LinkedBlockingQueue<ProductGroup> groups) {
        this.groups = groups;
    }

    public void addProductGroup(String name) {
        ProductGroup group = new ProductGroup(name);
        groups.add(group);
    }

    public boolean setPrice(String prodName, double price) {
        for (ProductGroup group : groups) {
            boolean isDone = group.ifPresentUpdate(prodName, price);
            if (isDone) return true;
        }
        return false;
    }

    public void addToGroup(String groupName, String productName) {
        for (ProductGroup group : groups)
            if (group.getTitle().equals(groupName))
                group.addGoods(new Product(productName));
    }

    public Integer getProdQ(String prodName) {
        for (ProductGroup group : groups) {
            Integer qOrNull = group.ifPresentGetQ(prodName);
            if (qOrNull != null)
                return qOrNull;
        }
        return null;
    }

    public boolean increaseProdQ(String prodName, int amount) {
        for (ProductGroup group : groups) {
            boolean isDone = group.ifPresentUpdate(prodName, amount);
            if (isDone) return true;
        }
        return false;
    }

    public boolean decreaseProdQ(String prodName, int amount) {
        for (ProductGroup group : groups) {
            boolean isDone = group.ifPresentUpdate(prodName, -amount);
            if (isDone) return true;
        }
        return false;
    }

}
