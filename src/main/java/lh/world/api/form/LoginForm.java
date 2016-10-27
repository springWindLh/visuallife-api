package lh.world.api.form;

/**
 * Created by lh on 2016/10/27.
 */
public class LoginForm {
    private String nameOrMobile;
    private String password;

    public LoginForm() {
    }

    public String getNameOrMobile() {
        return nameOrMobile;
    }

    public void setNameOrMobile(String nameOrMobile) {
        this.nameOrMobile = nameOrMobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
