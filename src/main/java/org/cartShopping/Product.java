package org.cartShopping;

import java.io.Serializable;

public class Product implements Serializable {

    private String name;
    private String description;
    private double price;
    private int quantity;
    private String imageRoute;

    public Product(String name, String description, double price, int quantity, String imageRoute) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.imageRoute = imageRoute;
    }

    // Getters y setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageRoute() {
        return imageRoute;
    }

    public void setImageRoute(String imageRoute) {
        this.imageRoute = imageRoute;
    }
}
