package org.example.DBOperationClass;

import org.example.entities.AllUserAndRollEntity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class UserDbControl{

    private static UserDbControl userDbControl = null;

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("user_database_config");
    EntityManager em = emf.createEntityManager();

    public static UserDbControl getInstance()
    {
        if(userDbControl == null)
            userDbControl = new UserDbControl();

        return userDbControl;
    }

    public synchronized void createNewUser(AllUserAndRollEntity allUserAndRollEntity)
    {
        if(!em.getTransaction().isActive())
        {
            em.getTransaction().begin();
        }

        em.persist(allUserAndRollEntity);
        em.getTransaction().commit();
    }

    public void removeUser(AllUserAndRollEntity allUserAndRollEntity)
    {
        em.getTransaction().begin();
        em.remove(allUserAndRollEntity);
        em.getTransaction().commit();
    }

    public void updateUser(AllUserAndRollEntity allUserAndRollEntity)
    {
        AllUserAndRollEntity allUserAndRollEntity1 = em.find(AllUserAndRollEntity.class, allUserAndRollEntity.getId());

        em.getTransaction().begin();
        allUserAndRollEntity1.setId(allUserAndRollEntity.getId());
        allUserAndRollEntity1.setImage(allUserAndRollEntity.getImage());
        allUserAndRollEntity1.setName(allUserAndRollEntity.getName());
        allUserAndRollEntity1.setPassword(allUserAndRollEntity.getPassword());
        allUserAndRollEntity1.setRole(allUserAndRollEntity.getRole());
        em.getTransaction().commit();
    }

    public void findUser(String s, String s1) {
        em.
    }
}
