package org.DBOperation;

import org.entities.AllUserAndRollEntity;
import sharedClasses.LoginReq;
import sharedClasses.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserDbControl{

    private static UserDbControl userDbControl = null;

    EntityManagerFactory emf = Persistence.createEntityManagerFactory("user_database_config");
    EntityManager em = emf.createEntityManager();

    private UserDbControl() {
    }

    public static UserDbControl getInstance()
    {
        if(userDbControl == null)
            userDbControl = new UserDbControl();
        return userDbControl;
    }

    public synchronized String createNewUser(AllUserAndRollEntity allUserAndRollEntity)
    {
        if(!em.getTransaction().isActive())
        {
            em.getTransaction().begin();
        }
        try
        {
            em.persist(allUserAndRollEntity);
            em.getTransaction().commit();
            return "success";
        }catch (Exception e)
        {
            return "failed";
        }
    }

    public synchronized void removeUser(AllUserAndRollEntity allUserAndRollEntity)
    {
        em.getTransaction().begin();
        em.remove(allUserAndRollEntity);
        em.getTransaction().commit();
    }

    public synchronized void updateUser(AllUserAndRollEntity allUserAndRollEntity)
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

    public synchronized List<User> getAllUser()
    {
        em.getTransaction().begin();
        List<AllUserAndRollEntity> allUserAndRollEntityList= em.createQuery("SELECT t from AllUserAndRollEntity t").getResultList();
        em.getTransaction().commit();
        List<User> userList = new ArrayList<>();

        for(AllUserAndRollEntity allUserAndRollEntity : allUserAndRollEntityList)
        {
            User user = manipulate(allUserAndRollEntity);
            userList.add(user);
        }

        return userList;
    }

    public synchronized User userLoginChecker(LoginReq loginReq) {

        Query query = em.createQuery(
                "SELECT c FROM AllUserAndRollEntity c WHERE c.name = :username");

        query.setParameter("username", loginReq.getUserName());

        List<AllUserAndRollEntity> resultList = query.getResultList();

        if(loginReq.getPassword().isEmpty()) //As viewer will not have any password
        {
            for(AllUserAndRollEntity allUserAndRollEntity : resultList)
            {
                if(allUserAndRollEntity.getRole().equals("Viewer"))
                {
                    return manipulate(allUserAndRollEntity);
                }
            }
        }
        else//This is for admin and manufacturer checking
        {
            for(AllUserAndRollEntity allUserAndRollEntity : resultList)
            {
                if(allUserAndRollEntity.getPassword().equals(loginReq.getPassword()))
                {
                    return manipulate(allUserAndRollEntity);
                }
            }
        }

        return new User();
    }

    public User manipulate(AllUserAndRollEntity allUserAndRollEntity)//Getting the user response ready
    {
        User user = new User();

        user.setSuccessful(true);
        user.setName(allUserAndRollEntity.getName());
        user.setRole(allUserAndRollEntity.getRole());
        user.setUser_id(allUserAndRollEntity.getId());

        List<String> actions = new ArrayList<>();

        if(allUserAndRollEntity.getRole().equals("Admin"))
        {
            actions.add("Create a new user");
            actions.add("Update existence user");
            actions.add("Remove existence user");
        }

        else if(allUserAndRollEntity.getRole().equals("Viewer"))
        {
            actions.add("View all cars");
            actions.add("Search car by registration number");
            actions.add("Search car by make and model");
            actions.add("Buy a car");
        }

        else if(allUserAndRollEntity.getRole().equals("Manufacturer"))
        {
            actions.add("View all cars");
            actions.add("Add a car");
            actions.add("Edit a car");
            actions.add("Delete a car");
        }

        user.setActions(actions);

        try {
            File file = new File(allUserAndRollEntity.getImage());
            FileInputStream fileInputStream = new FileInputStream(file.getPath());
            user.setByteArraySize((int) file.length());
            fileInputStream.read(user.getImage(), 0, user.getImage().length);
            fileInputStream.close();

        } catch (IOException e) {
            user.setSuccessful(false);
            e.printStackTrace();
        }
        return user;
    }
}
