package lh.world.api.controller;

import com.google.common.base.Strings;
import lh.world.api.controller.support.AjaxResponse;
import lh.world.api.controller.support.BaseController;
import lh.world.api.form.CommentForm;
import lh.world.base.domain.Comment;
import lh.world.base.domain.User;
import lh.world.base.query.support.Query;
import lh.world.base.service.CommentService;
import lh.world.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Created by lh on 2016/9/18.
 */
@RestController
@RequestMapping("/comment")
public class CommentController extends BaseController {
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public AjaxResponse list(Query query, String targetType, Long targetId) {
        Comment.TargetType type = null;
        if (!Strings.isNullOrEmpty(targetType)) {
            type = Comment.TargetType.valueOf(targetType);
        }
        query.setDirection(Sort.Direction.ASC);
        Page<Comment> page = commentService.listByTargetTypeAndTargetId(type, targetId, false, query);
        return AjaxResponse.ok().data(page);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public AjaxResponse add(@RequestBody @Valid CommentForm form, BindingResult result) {
        if (result.hasErrors()) {
            return getErrorInfo(result);
        }
        Comment comment = form.asComment();
        Optional<User> userOptional = userService.findById(form.getUser().getId());
        userOptional.ifPresent(comment::setUser);
        try {
            comment = commentService.save(comment);
            return AjaxResponse.ok().msg("发表成功").data(comment);
        } catch (Exception e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public AjaxResponse remove(@PathVariable Long id) {
        try {
            commentService.remove(id);
            return AjaxResponse.ok().msg("删除成功");
        } catch (Exception e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }

    @RequestMapping(value = "/vote/{id}", method = RequestMethod.POST)
    public AjaxResponse vote(@PathVariable Long id) {
        Optional<Comment> commentOptional = commentService.findById(id);
        if (!commentOptional.isPresent()) {
            return getAjaxResourceNotFound();
        }
        Comment comment = commentOptional.get();
        comment.setVote(comment.getVote() + 1);
        try {
            comment = commentService.save(comment);
            return AjaxResponse.ok().msg("点赞成功").data(comment);
        } catch (Exception e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public AjaxResponse count(String targetType, Long targetId) {
        Comment.TargetType type = null;
        if (!Strings.isNullOrEmpty(targetType)) {
            type = Comment.TargetType.valueOf(targetType);
        }
        Query query = new Query();
        query.setSize(Integer.MAX_VALUE);
        Page<Comment> page = commentService.listByTargetTypeAndTargetId(type, targetId, false, query);
        return AjaxResponse.ok().data(page.getTotalElements());
    }
}
