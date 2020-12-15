package org.dboperation;

import org.entities.Cars;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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

    /*
    public synchronized void updateCar(Cars cars)
    {
        Cars cars1 = em.find(Cars.class, cars.getCarReg());

        String image = cars1.getImage();

        em.getTransaction().begin();

        allUserAndRollEntity1.setId(allUserAndRollEntity.getId());
        allUserAndRollEntity1.setName(allUserAndRollEntity.getName());
        allUserAndRollEntity1.setPassword(allUserAndRollEntity.getPassword());
        allUserAndRollEntity1.setRole(allUserAndRollEntity.getRole());
        allUserAndRollEntity1.setImage(image);
        em.getTransaction().commit();
    }

     */

}
