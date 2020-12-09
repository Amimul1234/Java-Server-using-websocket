package sharedClasses;

import java.io.Serial;
import java.io.Serializable;

public class LoginReq implements Serializable {
    @Serial
    private final long serialUID = 5641515241541L;
    private String userName;
    private String password;

    public LoginReq() {
    }

    public LoginReq(String userName, String password, boolean isSuccessful) {
        this.userName = userName;
        this.password = password;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
