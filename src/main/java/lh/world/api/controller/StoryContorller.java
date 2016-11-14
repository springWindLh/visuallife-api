package lh.world.api.controller;

import lh.world.api.controller.support.AjaxResponse;
import lh.world.api.controller.support.BaseController;
import lh.world.base.domain.Story;
import lh.world.base.domain.User;
import lh.world.base.query.support.Query;
import lh.world.base.service.StoryService;
import lh.world.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * Created by lh on 2016/10/18.
 */
@RestController
@RequestMapping("/story")
public class StoryContorller extends BaseController {
    @Autowired
    StoryService storyService;
    @Autowired
    UserService userService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public AjaxResponse list(Query query) {
        Page<Story> page = storyService.listAll(query);
        return AjaxResponse.ok().data(page);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AjaxResponse detail(@PathVariable Long id) {
        Optional<Story> storyOptional = storyService.findById(id);
        if (!storyOptional.isPresent()) {
            return getAjaxResourceNotFound();
        }
        return AjaxResponse.ok().data(storyOptional.get());
    }

    @RequestMapping(value = "/vote/{id}", method = RequestMethod.POST)
    public AjaxResponse vote(@PathVariable Long id) {
        Optional<Story> storyOptional = storyService.findById(id);
        if (!storyOptional.isPresent()) {
            return getAjaxResourceNotFound();
        }
        Story story = storyOptional.get();
        int vote = story.getVote() != null ? story.getVote() : 0;
        story.setVote(vote + 1);
        try {
            story = storyService.save(story);
            return AjaxResponse.ok().msg("点赞成功").data(story);
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
        Page<Story> page = storyService.listByUser(userOptional.get(), query, false);
        return AjaxResponse.ok().data(page);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse remove(@RequestBody Long[] ids) {
        try {
            storyService.remove(ids);
            return AjaxResponse.ok().msg("删除成功");
        } catch (Exception e) {
            return AjaxResponse.fail().msg(e.getMessage());
        }
    }
}
