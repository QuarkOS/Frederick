package org.example;

import java.util.ArrayList;
import java.util.List;

public class Order {

    public List<Item> items;

    public Order(List<Item> items) {
        this.items = new ArrayList<>(items);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String itemTemplate = "%s x%d\n";
        for (Item item : items) {
            sb.append(itemTemplate.formatted(item.getName(), item.getQuantity()));
        }

        return sb.toString();
    }
}
