package org.example.DBOperationClass;

import org.example.entities.AllUserAndRollEntity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class UserDbControl{

    private static UserDbControl userDbControl = null;


    private UserDbControl() {
    }

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

    public List<String> findUser(String userName, String password) {

        Query query = em.createQuery(
                "SELECT c FROM AllUserAndRollEntity c WHERE c.name = :username");

        query.setParameter("username", userName);

        List<AllUserAndRollEntity> resultList = query.getResultList();

        for(AllUserAndRollEntity allUserAndRollEntity : resultList)
        {
             if(allUserAndRollEntity.getPassword().equals(password))
            {
                List<String> response = new ArrayList<>();

                response.add("false");//This is for error indication
                response.add(allUserAndRollEntity.getName());
                response.add(allUserAndRollEntity.getImage());
                response.add(allUserAndRollEntity.getRole());

                return response;
            }
        }

        List<String> response = new ArrayList<>();
        response.add("true");

        return response;
    }
}
