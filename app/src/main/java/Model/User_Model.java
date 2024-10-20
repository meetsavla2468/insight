package Model;

public class User_Model {
    String userName,email,password,profile_pic,profile_bg;

    public User_Model(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getProfile_bg() {
        return profile_bg;
    }

    public void setProfile_bg(String profile_bg) {
        this.profile_bg = profile_bg;
    }

    public User_Model() {
    }
}
