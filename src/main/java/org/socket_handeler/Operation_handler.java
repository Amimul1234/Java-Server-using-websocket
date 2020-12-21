package org.socket_handeler;

import org.dboperation.CarDbControl;
import org.dboperation.UserDbControl;
import org.entities.AllUserAndRollEntity;
import org.entities.Cars;
import sharedClasses.Car_shared;
import sharedClasses.LoginReq;
import sharedClasses.User;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Operation_handler {

    private UserDbControl userDbControl;
    private CarDbControl carDbControl;
    private final String server_image_directory = "H:/sever_image/";

    public Operation_handler(Socket socket, UserDbControl userDbControl, CarDbControl carDbControl) {
        this.userDbControl = userDbControl;
        this.carDbControl = carDbControl;
        necessaryActionTaker(socket);
    }

    private void necessaryActionTaker(Socket socket) //User role checker function
    {
        ClientHandlerGeneral clientHandlerGeneral = new ClientHandlerGeneral(socket);

        String role = null;

        while(role == null)
        {
            role = credentialChecker(clientHandlerGeneral); //Checking for credentials

            if(role != null)
            {
                if(role.equals("Admin"))
                {
                    adminControls(clientHandlerGeneral);
                }
                else if(role.equals("Viewer"))
                {
                    viewerControls(clientHandlerGeneral); //Viewer controls
                }
                else
                {
                    manufacturerControls(clientHandlerGeneral); //manufacturer controls
                }
            }

            else
            {
                role =  credentialChecker(clientHandlerGeneral);
            }
        }
    }

    private void viewerControls(ClientHandlerGeneral clientHandlerGeneral)
    {
        Cars cars = new Cars();
        String manufacturer_req_from_client;

        try {
            ObjectInputStream objectInputStream = clientHandlerGeneral.getObjectInputStream();

            manufacturer_req_from_client = (String) objectInputStream.readUnshared();

            switch (manufacturer_req_from_client) {

                case "1" -> {

                    List<Car_shared> car_sharedList = new ArrayList<>(carDbControl.getAllCar());

                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                    objectOutputStream.writeObject(car_sharedList);
                    objectOutputStream.flush();

                    String message = (String) clientHandlerGeneral.getObjectInputStream().readObject();

                    if(message.equals("back to main menu"))
                    {
                        viewerControls(clientHandlerGeneral);
                    }

                }
                case "2"->{
                    while(true)
                    {

                        String registration_number = (String) objectInputStream.readObject();

                        if(registration_number.equals("Back to main menu"))
                        {
                            viewerControls(clientHandlerGeneral);
                            break;
                        }

                        else
                        {
                            Car_shared car_shared = carDbControl.findCarByReg(registration_number);

                            if(car_shared != null)
                            {
                                ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                                objectOutputStream.writeObject(car_shared);
                                objectOutputStream.flush();
                            }
                            else
                            {
                                ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                                String message = new String("Car with given registration number does not exists");
                                objectOutputStream.writeObject(message);
                                objectOutputStream.flush();
                            }
                        }
                    }
                }

                case "3"->{
                    while(true)
                    {
                        String registration_number = (String) objectInputStream.readObject();

                        if(registration_number.equals("Back to main menu"))
                        {
                            viewerControls(clientHandlerGeneral);
                            break;
                        }
                        else
                        {
                            String[] carmake = registration_number.split("____");

                            List<Car_shared> car_sharedList = carDbControl.findCarListByCarMake(carmake[0]);

                            if(car_sharedList != null)
                            {
                                if(carmake[1].equalsIgnoreCase("any"))
                                {
                                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                                    objectOutputStream.writeObject(car_sharedList);
                                    objectOutputStream.flush();
                                }
                                else
                                {
                                    List<Car_shared> modified_car_shared = new ArrayList<>();

                                    for(Car_shared car_shared : car_sharedList)
                                    {
                                        if(car_shared.getCarModel().equalsIgnoreCase(carmake[1]))
                                        {
                                            modified_car_shared.add(car_shared);
                                        }
                                    }

                                    if(modified_car_shared.size() > 0)
                                    {
                                        ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                                        objectOutputStream.writeObject(modified_car_shared);
                                        objectOutputStream.flush();
                                    }
                                    else
                                    {
                                        ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                                        String message = new String("Car with given car make does not exists");
                                        objectOutputStream.writeObject(message);
                                        objectOutputStream.flush();
                                    }
                                }
                            }

                            else
                            {
                                ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                                String message = new String("Car with given car make does not exists");
                                objectOutputStream.writeObject(message);
                                objectOutputStream.flush();
                            }
                        }
                    }
                }
                case "4"->{

                    List<Car_shared> car_sharedList = new ArrayList<>(carDbControl.getAllCar());

                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                    objectOutputStream.writeObject(car_sharedList);
                    objectOutputStream.flush();

                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();

                    try {

                        Car_shared car_shared = (Car_shared) objectInputStream1.readObject();

                        Cars cars1 = new Cars();

                        cars1.setCarReg(car_shared.getCarReg());
                        cars1.setQuantity(car_shared.getQuantity());
                        cars1.setYearMade(car_shared.getYearMade());
                        cars1.setColour1(car_shared.getColour1());
                        cars1.setColour2(car_shared.getColour2());
                        cars1.setColour3(car_shared.getColour3());
                        cars1.setCarMake(car_shared.getCarMake());
                        cars1.setCarModel(car_shared.getCarModel());
                        cars1.setPrice(car_shared.getPrice());

                        carDbControl.reduceQuantity(cars1);

                        notify_client_about_updated_car_list();

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    viewerControls(clientHandlerGeneral);

                }
            }
        }catch (Exception e) {
                e.printStackTrace();
            }

    }

    private void manufacturerControls(ClientHandlerGeneral clientHandlerGeneral)
    {
        Cars cars = new Cars();
        String manufacturer_req_from_client;

        try {
            ObjectInputStream objectInputStream = clientHandlerGeneral.getObjectInputStream();

            manufacturer_req_from_client = (String) objectInputStream.readUnshared();

            switch (manufacturer_req_from_client)
            {
                case "1"-> {

                    List<Car_shared> car_sharedList = new ArrayList<>(carDbControl.getAllCar());

                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                    objectOutputStream.writeObject(car_sharedList);
                    objectOutputStream.flush();

                    String message = (String) clientHandlerGeneral.getObjectInputStream().readObject();

                    if(message.equals("back to main menu"))
                    {
                        manufacturerControls(clientHandlerGeneral);
                    }
                }
                case "2"->{

                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();
                    Car_shared car_shared = (Car_shared) objectInputStream1.readObject();

                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    String directory = server_image_directory + timeStamp + ".jpg"; //Giving unique name by using timestamp

                    FileOutputStream fileOutputStream = new FileOutputStream(directory);
                    fileOutputStream.write(car_shared.getCarImage(), 0, car_shared.getCarImage().length);
                    fileOutputStream.close();

                    cars.setCarReg(car_shared.getCarReg());
                    cars.setQuantity(car_shared.getQuantity());
                    cars.setYearMade(car_shared.getYearMade());
                    cars.setColour1(car_shared.getColour1());
                    cars.setColour2(car_shared.getColour2());
                    cars.setColour3(car_shared.getColour3());
                    cars.setCarMake(car_shared.getCarMake());
                    cars.setCarModel(car_shared.getCarModel());
                    cars.setPrice(car_shared.getPrice());
                    cars.setImage(directory);

                    if (carDbControl.createNewCar(cars).equals("success")) {

                        ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                        String response = "success";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();

                        notify_client_about_updated_car_list();

                        manufacturerControls(clientHandlerGeneral);

                    } else {
                        ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                        String response = "failed";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();

                        adminControls(clientHandlerGeneral);
                    }
                }

                case "3"->{

                    List<Car_shared> car_sharedList = new ArrayList<>(carDbControl.getAllCar());

                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                    objectOutputStream.writeObject(car_sharedList);
                    objectOutputStream.flush();

                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();

                    try {

                        Car_shared car_shared = (Car_shared) objectInputStream1.readObject();

                        Cars cars1 = new Cars();

                        cars1.setCarReg(car_shared.getCarReg());
                        cars1.setQuantity(car_shared.getQuantity());
                        cars1.setYearMade(car_shared.getYearMade());
                        cars1.setColour1(car_shared.getColour1());
                        cars1.setColour2(car_shared.getColour2());
                        cars1.setColour3(car_shared.getColour3());
                        cars1.setCarMake(car_shared.getCarMake());
                        cars1.setCarModel(car_shared.getCarModel());
                        cars1.setPrice(car_shared.getPrice());
                        carDbControl.updateCar(cars1);

                        notify_client_about_updated_car_list();

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    manufacturerControls(clientHandlerGeneral);//Going to main menu again
                }

                case "4"->{

                    List<Car_shared> car_sharedList = new ArrayList<>(carDbControl.getAllCar());

                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                    objectOutputStream.writeObject(car_sharedList);
                    objectOutputStream.flush();

                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();

                    try {

                        Car_shared car_shared = (Car_shared) objectInputStream1.readObject();

                        Cars cars1 = new Cars();
                        cars1.setCarReg(car_shared.getCarReg());

                        carDbControl.removeCar(cars1);

                        notify_client_about_updated_car_list();

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    manufacturerControls(clientHandlerGeneral);//Going to main menu again

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adminControls(ClientHandlerGeneral clientHandlerGeneral) //This is on different thread for every connection
    {

        AllUserAndRollEntity allUserAndRollEntity = new AllUserAndRollEntity();
        String admin_req_from_client;

        try {

            ObjectInputStream objectInputStream = clientHandlerGeneral.getObjectInputStream();
            admin_req_from_client = (String) objectInputStream.readUnshared();

            switch (admin_req_from_client) {

                case "1" -> {

                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();
                    User user = (User) objectInputStream1.readObject();

                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    String directory = server_image_directory + timeStamp + ".jpg"; //Giving unique name by using timestamp

                    FileOutputStream fileOutputStream = new FileOutputStream(directory);
                    fileOutputStream.write(user.getImage(), 0, user.getImage().length);
                    fileOutputStream.close();

                    allUserAndRollEntity.setName(user.getName());
                    allUserAndRollEntity.setRole(user.getRole());
                    allUserAndRollEntity.setImage(directory);
                    allUserAndRollEntity.setPassword(user.getPassword());

                    if (userDbControl.createNewUser(allUserAndRollEntity).equals("success")) {

                        ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                        String response = "success";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();

                        notify_client_about_updated_user_list();

                        adminControls(clientHandlerGeneral);

                    } else {
                        ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                        String response = "failed";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();

                        adminControls(clientHandlerGeneral);
                    }
                }

                case "2" -> {

                    List<User> userList = new ArrayList<>(userDbControl.getAllUser());
                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                    objectOutputStream.writeObject(userList);
                    objectOutputStream.flush();

                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();

                    try {
                        User user = (User) objectInputStream1.readObject();
                        AllUserAndRollEntity allUserAndRollEntity1 = new AllUserAndRollEntity();
                        allUserAndRollEntity1.setRole(user.getRole());
                        allUserAndRollEntity1.setName(user.getName());
                        allUserAndRollEntity1.setPassword(user.getPassword());
                        allUserAndRollEntity1.setId(user.getUser_id());

                        userDbControl.updateUser(allUserAndRollEntity1);

                        notify_client_about_updated_user_list();

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    adminControls(clientHandlerGeneral);//Going to main menu again

                }
                case "3" -> {

                    List<User> userList = new ArrayList<>(userDbControl.getAllUser());
                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                    objectOutputStream.writeObject(userList);
                    objectOutputStream.flush();

                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();

                    try {
                        User user = (User) objectInputStream1.readObject();
                        AllUserAndRollEntity allUserAndRollEntity1 = new AllUserAndRollEntity();
                        allUserAndRollEntity1.setId(user.getUser_id());
                        userDbControl.removeUser(allUserAndRollEntity1);

                        notify_client_about_updated_user_list();

                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    adminControls(clientHandlerGeneral);//Going to main menu again
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String credentialChecker(ClientHandlerGeneral socket)
    {
        try {
            ObjectInputStream objectInputStream = socket.getObjectInputStream();
            LoginReq loginReq = (LoginReq) objectInputStream.readObject();//Getting loginReq object from user

            User user = userDbControl.userLoginChecker(loginReq);

            ObjectOutputStream objectOutputStream = socket.getObjectOutputStream();
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();//Writing response to the user

            if (user.isSuccessful())
                return user.getRole();
            else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            return "connection reset";
        }
    }

    private void notify_client_about_updated_user_list()
    {
        List<User> userList = new ArrayList<>(userDbControl.getAllUser()); //Getting the updated list of client

        int size = ServerSocketHandler.clientHandlerUserListUpdateArrayList.size();

        for(int i=0; i<size; i++)
        {
            try
            {
                ServerSocketHandler.clientHandlerUserListUpdateArrayList.get(i).getObjectOutputStream().writeObject(userList);
                ServerSocketHandler.clientHandlerUserListUpdateArrayList.get(i).getObjectOutputStream().flush();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void notify_client_about_updated_car_list()
    {
        List<Car_shared> car_sharedList = new ArrayList<>(carDbControl.getAllCar()); //Getting the updated list of client

        int size = ServerSocketHandler.clientHandlerCarUpdateList.size();

        for(int i=0; i<size; i++)
        {
            try
            {
                ServerSocketHandler.clientHandlerCarUpdateList.get(i).getObjectOutputStream().writeObject(car_sharedList);
                ServerSocketHandler.clientHandlerCarUpdateList.get(i).getObjectOutputStream().flush();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
