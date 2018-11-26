package com.example.khem.javaproject.Model;

public class Food {

    private String ID, Name, Image, Description, Price, Category, Key;

    public Food() {}

    public Food(String id, String name, String description, String price, String category, String image) {
        this.ID = id;
        this.Name = name;
        this.Description = description;
        this.Price = price;
        this.Category = category;
        this.Image = image;


    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPrice() {
        return Price;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getCategory() {
        return Category;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getKey() {
        return Key;
    }
}