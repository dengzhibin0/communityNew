package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping(path = "/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.GET)
    @ResponseBody
    @LoginRequired
    public String addDiscussPost() {
        return null;
    }

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        // 检查用户是否登录
        if (user == null) {
            return CommunityUtil.getJSONString(403, "请先登录！");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况之后统一处理
        // TODO
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);

        // 作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        // 评论分页
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        // 评论：给帖子的评论
        // 回复：给评论的评论
        // 评论列表
        List<Comment> commentList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> comments = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 每个评论的信息
                Map<String, Object> commentMap = new HashMap<>();
                commentMap.put("comment", comment);
                commentMap.put("user", userService.findUserById(comment.getUserId()));

                // 查询当前评论的评论（回复）
                List<Comment> replayList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replays = new ArrayList<>();
                if (replayList != null) {
                    for (Comment replay : replayList) {
                        // 每个回复的信息
                        Map<String, Object> replayMap = new HashMap<>();
                        replayMap.put("replay", replay);
                        replayMap.put("user", userService.findUserById(replay.getUserId()));

                        // 每个回复的目标
                        User target = replay.getTargetId() == 0 ? null : userService.findUserById(replay.getTargetId());
                        replayMap.put("target", target);
                        replays.add(replayMap);
                    }
                }
                commentMap.put("replays", replays);

                // 回复数量
                int replayCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentMap.put("replayCount", replayCount);
                comments.add(commentMap);
            }
        }
        model.addAttribute("comments", comments);

        return "/site/discuss-detail";
    }

}
