package org.dboperation;

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


}
