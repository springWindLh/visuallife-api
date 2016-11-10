package lh.world.api.form;

import lh.world.base.domain.User;

/**
 * Created by lh on 2016/11/8.
 */
public class ReplyForm extends lh.world.base.form.ReplyForm {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
