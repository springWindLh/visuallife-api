package lh.world.api.controller;

import com.google.common.base.Strings;
import lh.world.api.controller.support.AjaxResponse;
import lh.world.api.controller.support.BaseController;
import lh.world.base.domain.User;
import lh.world.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by lh on 2016/10/26.
 */
@RestController
public class LoginController extends BaseController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public AjaxResponse login(@RequestParam String nameOrMobile, @RequestParam String password) {
        final String errorMsg = "用户名或密码错误";
        if (Strings.isNullOrEmpty(nameOrMobile) || Strings.isNullOrEmpty(password)) {
            return AjaxResponse.fail().msg(errorMsg);
        }
        Optional<User> userOptional = userService.verifyUser(nameOrMobile, nameOrMobile, password);
        if (userOptional.isPresent()) {
            getRequest().getSession().setAttribute("user", userOptional.get());
            return AjaxResponse.ok().msg("登录成功");
        } else {
            return AjaxResponse.fail().msg(errorMsg);
        }
    }
}
