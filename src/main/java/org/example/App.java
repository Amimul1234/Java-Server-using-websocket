package org.example;

import org.example.DBOperationClass.UserDbControl;
import org.example.entities.AllUserAndRollEntity;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        UserDbControl userDbControl = UserDbControl.getInstance();

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(1242);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true)
        {
            try {
                Socket socket;
                if (serverSocket != null) {
                    socket = serverSocket.accept();

                    new Thread()
                    {
                        @Override
                        public void run() {
                            try {
                                InputStream inputStream = socket.getInputStream();

                                credentialChecker(userDbControl, inputStream); //Checking for role and credential

                                String[] user1 = new String[4];

                                user1 = (String[]) objectInputStream.readObject();


                                AllUserAndRollEntity allUserAndRollEntity = new AllUserAndRollEntity();

                                allUserAndRollEntity.setName(user1[0]);
                                allUserAndRollEntity.setPassword(user1[1]);
                                allUserAndRollEntity.setRole(user1[2]);
                                allUserAndRollEntity.setImage(user1[3]);

                                userDbControl.createNewUser(allUserAndRollEntity);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void credentialChecker(UserDbControl userDbControl, InputStream inputStream)
    {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            List<String> credential  = (List<String>) objectInputStream.readObject();

            userDbControl.findUser(credential.get(0), credential.get(1));


        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
