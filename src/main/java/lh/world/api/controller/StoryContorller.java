package lh.world.api.controller;

import lh.world.api.controller.support.AjaxResponse;
import lh.world.api.controller.support.BaseController;
import lh.world.base.domain.Story;
import lh.world.base.query.support.Query;
import lh.world.base.service.StoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by lh on 2016/10/18.
 */
@RestController
@RequestMapping("/story")
public class StoryContorller extends BaseController {
    @Autowired
    StoryService storyService;

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
}
