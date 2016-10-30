package com.codesparts.toastappclient.model;

public class Ingredient {
    private String title, category, quantity;
    private boolean isChecked;

    public Ingredient() {
    }

    public Ingredient(String title, String category, String quantity) {
        this.title = title;
        this.category = category;
        this.quantity = quantity;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
