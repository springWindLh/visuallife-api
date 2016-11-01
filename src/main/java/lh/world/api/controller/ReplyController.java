package lh.world.api.controller;

import lh.world.api.controller.support.AjaxResponse;
import lh.world.api.controller.support.BaseController;
import lh.world.base.domain.Reply;
import lh.world.base.form.ReplyForm;
import lh.world.base.query.support.Query;
import lh.world.base.service.ReplyService;
import lh.world.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Created by lh on 2016/9/18.
 */
@RestController
@RequestMapping("/reply")
public class ReplyController extends BaseController {
    @Autowired
    ReplyService replyService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public AjaxResponse add(@RequestBody @Valid ReplyForm form, BindingResult result) {
        if (result.hasErrors()) {
            return getErrorInfo(result);
        }
        Reply reply = form.asReply();
        reply.setSender(getCurrentUser());
        try {
            reply = replyService.save(reply, form.getCommentId(), form.getAccepterId());
            return AjaxResponse.ok().msg("发表成功").data(reply);
        } catch (Exception e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST)
    public AjaxResponse remove(@PathVariable Long id) {
        try {
            replyService.remove(id);
            return AjaxResponse.ok().msg("删除成功");
        } catch (Exception e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }

    @RequestMapping(value = "/vote/{id}", method = RequestMethod.POST)
    public AjaxResponse vote(@PathVariable Long id) {
        Optional<Reply> replyOptional = replyService.findById(id);
        if (!replyOptional.isPresent()) {
            return getAjaxResourceNotFound();
        }
        Reply reply = replyOptional.get();
        reply.setVote(reply.getVote() + 1);
        try {
            reply = replyService.save(reply);
            return AjaxResponse.ok().msg("点赞成功").data(reply);
        } catch (Exception e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }
}
