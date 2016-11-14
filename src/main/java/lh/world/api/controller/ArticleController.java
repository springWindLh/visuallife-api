package lh.world.api.controller;

import lh.world.api.controller.support.AjaxResponse;
import lh.world.api.controller.support.BaseController;
import lh.world.api.query.ArticleQuery;
import lh.world.base.domain.Article;
import lh.world.base.domain.User;
import lh.world.base.query.support.Query;
import lh.world.base.service.ArticleService;
import lh.world.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * Created by lh on 2016/10/17.
 */
@RestController
@RequestMapping("/article")
public class ArticleController extends BaseController {
    @Autowired
    ArticleService articleService;
    @Autowired
    UserService userService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResponse list(ArticleQuery query) {
        Page<Article> page = articleService.listByTitle(query.getTitle(), query);
        return AjaxResponse.ok().data(page);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResponse detail(@PathVariable Long id) {
        Optional<Article> articleOptional = articleService.findById(id);
        if (!articleOptional.isPresent()) {
            return getAjaxResourceNotFound();
        }
        return AjaxResponse.ok().data(articleOptional.get());
    }

    @RequestMapping(value = "/vote/{id}", method = RequestMethod.POST)
    public AjaxResponse vote(@PathVariable Long id) {
        Optional<Article> articleOptional = articleService.findById(id);
        if (!articleOptional.isPresent()) {
            return getAjaxResourceNotFound();
        }
        Article article = articleOptional.get();
        int vote = article.getVote() != null ? article.getVote() : 0;
        article.setVote(vote + 1);
        try {
            article = articleService.save(article);
            return AjaxResponse.ok().msg("点赞成功").data(article);
        } catch (Exception e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }

    @RequestMapping(value = "/user/list/{userId}", method = RequestMethod.GET)
    public AjaxResponse userList(@PathVariable Long userId, Query query) {
        Optional<User> userOptional = userService.findById(userId);
        if (!userOptional.isPresent()) {
            return AjaxResponse.fail().msg("用户信息不存在");
        }
        Page<Article> page = articleService.listByUser(userOptional.get(), query, false);
        return AjaxResponse.ok().data(page);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    public AjaxResponse remove(@RequestBody Long[] ids) {
        try {
            articleService.remove(ids);
            return AjaxResponse.ok().msg("删除成功");
        } catch (Exception e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }
}
