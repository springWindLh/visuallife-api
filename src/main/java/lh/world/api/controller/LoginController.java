package lh.world.api.controller;

import com.google.common.base.Strings;
import lh.world.api.controller.support.AjaxResponse;
import lh.world.api.controller.support.BaseController;
import lh.world.api.form.LoginForm;
import lh.world.base.domain.User;
import lh.world.base.form.UserForm;
import lh.world.base.service.UserService;
import lh.world.base.util.EncrptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by lh on 2016/10/26.
 */
@RestController
public class LoginController extends BaseController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AjaxResponse login(@RequestBody LoginForm form) {
        final String errorMsg = "用户名或密码错误";
        String nameOrMobile = form.getNameOrMobile();
        String password = form.getPassword();
        if (Strings.isNullOrEmpty(nameOrMobile) || Strings.isNullOrEmpty(password)) {
            return AjaxResponse.fail().msg(errorMsg);
        }
        Optional<User> userOptional = userService.verifyUser(nameOrMobile, nameOrMobile, password);
        if (userOptional.isPresent()) {
            getRequest().getSession().setAttribute("user", userOptional.get());
            return AjaxResponse.ok().msg("登录成功").data(userOptional.get());
        } else {
            return AjaxResponse.fail().msg(errorMsg);
        }
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public AjaxResponse register(@RequestBody UserForm form, BindingResult result) {
        if (result.hasErrors()) {
            return getErrorInfo(result);
        }
        User user = form.asUser();
        user.setPassword(EncrptUtil.encodePassword(user.getPassword()));
        user.setRole(User.Role.USER);
        try {
             User currentUser = userService.save(user);
            return AjaxResponse.ok().msg("注册成功").data(currentUser);
        } catch (RuntimeException e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }
}
