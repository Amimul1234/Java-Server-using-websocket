package org.dboperation;

import org.entities.AllUserAndRollEntity;
import org.entities.Cars;
import sharedClasses.Car_shared;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CarDbControl {

    private static CarDbControl cardbcontrol = null;

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("user_database_config");
    EntityManager em = emf.createEntityManager();

    private CarDbControl() {
    }

    public static CarDbControl getInstance()
    {
        if(cardbcontrol == null)
            cardbcontrol = new CarDbControl();
        return cardbcontrol;
    }

    public synchronized String createNewCar(Cars cars)
    {
        try
        {
            em.getTransaction().begin();
            em.persist(cars);
            em.getTransaction().commit();
            return "success";
        }catch (Exception e)
        {
            return "failed";
        }
    }

    public synchronized void removeCar(Cars cars)
    {
        Cars cars1 = em.find(Cars.class, cars.getCarReg());
        em.getTransaction().begin();
        em.remove(cars1);
        em.getTransaction().commit();
    }

    public synchronized Car_shared findCarByReg(String registration_number)
    {
        try
        {
            return manipulate(em.find(Cars.class, registration_number));
        }catch (Exception e)
        {
            return null;
        }
    }

    public synchronized List<Car_shared> findCarListByCarMake(String s) {
        try
        {
            Query query = em.createQuery(
                    "SELECT c FROM Cars c WHERE c.CarMake = :carmake");

            query.setParameter("carmake", s);
            List<Cars> carsList = query.getResultList();
            List<Car_shared> car_sharedList = new ArrayList<>();

            for(Cars cars : carsList)
            {
                car_sharedList.add(manipulate(cars));
            }

            if(car_sharedList.size() > 0)
            {
                return car_sharedList;
            }
            else
            {
                return null;
            }

        }catch (Exception e)
        {
            return null;
        }
    }

    public synchronized void updateCar(Cars cars)
    {
        Cars car1 = em.find(Cars.class, cars.getCarReg());

        String image = car1.getImage();

        em.getTransaction().begin();

        car1.setCarReg(cars.getCarReg());
        car1.setQuantity(cars.getQuantity());
        car1.setYearMade(cars.getYearMade());
        car1.setColour1(cars.getColour1());
        car1.setColour2(cars.getColour2());
        car1.setColour3(cars.getColour3());
        car1.setCarMake(cars.getCarMake());
        car1.setCarModel(cars.getCarModel());
        car1.setPrice(cars.getPrice());
        car1.setImage(image);

        em.getTransaction().commit();
    }

    public synchronized List<Car_shared> getAllCar()
    {
        em.getTransaction().begin();

        List<Cars> carsList = em.createQuery("SELECT t from Cars t").getResultList();

        em.getTransaction().commit();

        List<Car_shared> car_sharedList = new ArrayList<>();

        for(Cars cars : carsList)
        {
            Car_shared car_shared = manipulate(cars);
            car_sharedList.add(car_shared);
        }

        return car_sharedList;
    }

    private Car_shared manipulate(Cars cars) {

        if(cars == null)
        {
            return null;
        }

        else
        {
            Car_shared car_shared = new Car_shared();

            car_shared.setCarReg(cars.getCarReg());
            car_shared.setQuantity(cars.getQuantity());
            car_shared.setYearMade(cars.getYearMade());
            car_shared.setColour1(cars.getColour1());
            car_shared.setColour2(cars.getColour2());
            car_shared.setColour3(cars.getColour3());
            car_shared.setCarMake(cars.getCarMake());
            car_shared.setCarModel(cars.getCarModel());
            car_shared.setPrice(cars.getPrice());

            try {
                File file = new File(cars.getImage());
                FileInputStream fileInputStream = new FileInputStream(file.getPath());
                car_shared.setByteArraySize((int) file.length());
                fileInputStream.read(car_shared.getCarImage(), 0, car_shared.getCarImage().length);
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return car_shared;

        }
    }

    public synchronized void reduceQuantity(Cars cars1) {

        Cars car1 = em.find(Cars.class, cars1.getCarReg());

        String image = car1.getImage();

        em.getTransaction().begin();

        car1.setCarReg(cars1.getCarReg());
        car1.setQuantity(cars1.getQuantity());
        car1.setYearMade(cars1.getYearMade());
        car1.setColour1(cars1.getColour1());
        car1.setColour2(cars1.getColour2());
        car1.setColour3(cars1.getColour3());
        car1.setCarMake(cars1.getCarMake());
        car1.setCarModel(cars1.getCarModel());
        car1.setPrice(cars1.getPrice());
        car1.setImage(image);

        em.getTransaction().commit();
    }
}
