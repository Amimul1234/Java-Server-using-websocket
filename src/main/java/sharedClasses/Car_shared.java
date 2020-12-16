package sharedClasses;

import java.io.Serial;
import java.io.Serializable;

public class Car_shared implements Serializable {
    @Serial
    private final long serialUID = 5641515L;
    private int quantity;
    private String CarReg;
    private int YearMade;
    private String Colour1;
    private String Colour2;
    private String Colour3;
    private String CarMake;
    private String CarModel;
    private int Price;
    private byte[] carImage;

    public Car_shared() {
    }

    public Car_shared(int quantity, String carReg, int yearMade, String colour1,
                      String colour2, String colour3, String carMake, String carModel,
                      int price, byte[] carImage)
    {
        this.quantity = quantity;
        CarReg = carReg;
        YearMade = yearMade;
        Colour1 = colour1;
        Colour2 = colour2;
        Colour3 = colour3;
        CarMake = carMake;
        CarModel = carModel;
        Price = price;
        this.carImage = carImage;
    }

    public long getSerialUID() {
        return serialUID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public byte[] getCarImage() {
        return carImage;
    }

    public void setCarImage(byte[] carImage) {
        this.carImage = carImage;
    }

    public void setByteArraySize(int size)
    {
        carImage = new byte[size];
    }
}
