package com.example.fireauthlog;

// Buyer Seller Model Class that will helps us to save data in model
public class BuyerSalerModel {

    private String MealName;
    private String DateTime;
    private String Mealdescription;
    private String SalerAddres;
    private String SalerName;
    private String BuyerAddres;
    private String BuyerName;
    private String plates;
    private String price;
    private String picture;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPlates() {
        return plates;
    }

    public void setPlates(String plates) {
        this.plates = plates;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMealName() {
        return MealName;
    }

    public void setMealName(String mealName) {
        MealName = mealName;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public String getMealdescription() {
        return Mealdescription;
    }

    public void setMealdescription(String mealdescription) {
        Mealdescription = mealdescription;
    }

    public String getSalerAddres() {
        return SalerAddres;
    }

    public void setSalerAddres(String salerAddres) {
        SalerAddres = salerAddres;
    }

    public String getSalerName() {
        return SalerName;
    }

    public void setSalerName(String salerName) {
        SalerName = salerName;
    }

    public String getBuyerAddres() {
        return BuyerAddres;
    }

    public void setBuyerAddres(String buyerAddres) {
        BuyerAddres = buyerAddres;
    }

    public String getBuyerName() {
        return BuyerName;
    }

    public void setBuyerName(String buyerName) {
        BuyerName = buyerName;
    }
}
