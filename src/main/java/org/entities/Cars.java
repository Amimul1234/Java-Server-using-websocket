package org.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cars {
    @Id
    private String CarReg;
    private int quantity;
    private int YearMade;
    private String Colour1;
    private String Colour2;
    private String Colour3;
    private String CarMake;
    private String CarModel;
    private int Price;
    private String image;

    public Cars() {
    }

    public Cars(String carReg, int quantity, int yearMade,
                String colour1, String colour2, String colour3,
                String carMake, String carModel, int price, String image)
    {
        CarReg = carReg;
        this.quantity = quantity;
        YearMade = yearMade;
        Colour1 = colour1;
        Colour2 = colour2;
        Colour3 = colour3;
        CarMake = carMake;
        CarModel = carModel;
        Price = price;
        this.image = image;
    }

    public String getCarReg() {
        return CarReg;
    }

    public void setCarReg(String carReg) {
        CarReg = carReg;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getYearMade() {
        return YearMade;
    }

    public void setYearMade(int yearMade) {
        YearMade = yearMade;
    }

    public String getColour1() {
        return Colour1;
    }

    public void setColour1(String colour1) {
        Colour1 = colour1;
    }

    public String getColour2() {
        return Colour2;
    }

    public void setColour2(String colour2) {
        Colour2 = colour2;
    }

    public String getColour3() {
        return Colour3;
    }

    public void setColour3(String colour3) {
        Colour3 = colour3;
    }

    public String getCarMake() {
        return CarMake;
    }

    public void setCarMake(String carMake) {
        CarMake = carMake;
    }

    public String getCarModel() {
        return CarModel;
    }

    public void setCarModel(String carModel) {
        CarModel = carModel;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
