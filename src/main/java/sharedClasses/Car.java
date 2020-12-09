package sharedClasses;

import java.io.Serial;
import java.io.Serializable;

public class Car implements Serializable {
    @Serial
    private final long serialUID = 5641515L;
    private String CarReg;
    private int YearMade;
    private String Colour1;
    private String Colour2;
    private String Colour3;
    private String CarMake;
    private String CarModel;
    private int Price;

    public Car(String[] str) {
        CarReg = str[0];
        YearMade = Integer.parseInt(str[1]);
        Colour1 = str[2];
        Colour2 = str[3];
        Colour3 = str[4];
        CarMake = str[5];
        CarModel = str[6];
        Price = Integer.parseInt(str[7]);
    }

    public Car() {

    }

    public String getCarReg() {
        return CarReg;
    }

    public void setCarReg(String carReg) {
        CarReg = carReg;
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
}
