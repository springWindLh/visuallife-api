package lh.world.api.controller;

import lh.world.api.controller.support.AjaxResponse;
import lh.world.api.controller.support.BaseController;
import lh.world.api.query.ArticleQuery;
import lh.world.base.domain.Article;
import lh.world.base.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * Created by lh on 2016/10/17.
 */
@RestController
@RequestMapping("/article")
public class ArticleController extends BaseController {
    @Autowired
    ArticleService articleService;

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
}
