package app.model;

public class User {
    private Long id;
    private String password;
    private String nickname;
    private String email;
    private String userRole;

    public User(String password, String nickname, String email, String userRole) {
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.userRole = userRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getUserRole() {
        return userRole;
    }

    @Override
    public String toString() {
        return "User [userId=" + id + ", password=" + password + ", name=" + nickname + ", email=" + email + "]";
    }
}
