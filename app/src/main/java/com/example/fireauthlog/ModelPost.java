package com.example.fireauthlog;

// Model Class for Save Post Data
public class ModelPost {

    public String Address;
    public String MealName;
    public String Plates;
    public String Status;
    public String mealdescription;
    public String picture;
    public String price;


    public ModelPost() {
    }

    public String getMealName() {
        return MealName;
    }

    public String getPlates() {
        return Plates;
    }

    public String getStatus() {
        return Status;
    }

    public String getMealdescription() {
        return mealdescription;
    }

    public String getPicture() {
        return picture;
    }

    public String getPrice() {
        return price;
    }


    public void setMealName(String mealName) {
        MealName = mealName;
    }

    public void setPlates(String plates) {
        Plates = plates;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public void setMealdescription(String mealdescription) {
        this.mealdescription = mealdescription;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
