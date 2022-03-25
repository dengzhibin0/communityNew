package com.nowcoder.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
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
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements CommunityConstant {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/letter/list", method = RequestMethod.GET)
    public String getLetterList(Model model, Page page) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationRows(user.getId()));

        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {

                // 每个会话的信息
                Map<String, Object> conversationMap = new HashMap<>();
                conversationMap.put("conversation", message);
                conversationMap.put("unreadCount", messageService.findUnreadLatterCount(user.getId(), message.getConversationId()));
                conversationMap.put("letterCount", messageService.findLatterCount(message.getConversationId()));
                int targetId = user.getId() == message.getFromId() ? message.getToId() : message.getFromId();
                conversationMap.put("target", userService.findUserById(targetId));
                conversations.add(conversationMap);
            }
        }
        model.addAttribute("conversations", conversations);

        // 查询未读消息数量
        int underCount = messageService.findUnreadLatterCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", underCount);

        // 查询未读通知的数量
        int noticeUnreadCount = messageService.findUnderNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        return "/site/letter";
    }

    @RequestMapping(path = "/letter/detail/{conversationId}", method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLatterCount(conversationId));

        List<Message> letterList = messageService.findLatters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>();
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("target", getTargetId(conversationId));

        // 将私信列表中的未读消息提取出来，并设置为已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    private User getTargetId(String conversationId) {
        String[] s = conversationId.split("_");
        int id0 = Integer.parseInt(s[0]);
        int id1 = Integer.parseInt(s[1]);
        if (id0 == hostHolder.getUser().getId()) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    // 得到消息集合中未读消息的id
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    @RequestMapping(path = "/letter/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }

        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        } else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);

        return CommunityUtil.getJSONString(0);
    }

    @RequestMapping(path = "/notice/list", method = RequestMethod.GET)
    public String getNoticeList(Model model) {
        User user = hostHolder.getUser();

        // 查询评论类通知
        Message message = messageService.findLatestNotice(user.getId(), TOPIC_COMMENT);
        Map<String, Object> messageMap;
        if (message != null) {
            messageMap = new HashMap<>();
            messageMap.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());  // 反转，将转义字符还原
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageMap.put("user", userService.findUserById((Integer) data.get("userId")));
            messageMap.put("entityType", data.get("entityType"));
            messageMap.put("entityId", data.get("entityId"));
            messageMap.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_COMMENT);
            messageMap.put("count", count);

            int unreadCount = messageService.findUnderNoticeCount(user.getId(), TOPIC_COMMENT);
            messageMap.put("unreadCount", unreadCount);
            model.addAttribute("commentNotice", messageMap);
        }

        // 查询点赞类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_LIKE);
        if (message != null) {
            messageMap = new HashMap<>();
            messageMap.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());  // 反转，将转义字符还原
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageMap.put("user", userService.findUserById((Integer) data.get("userId")));
            messageMap.put("entityType", data.get("entityType"));
            messageMap.put("entityId", data.get("entityId"));
            messageMap.put("postId", data.get("postId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_LIKE);
            messageMap.put("count", count);

            int unreadCount = messageService.findUnderNoticeCount(user.getId(), TOPIC_LIKE);
            messageMap.put("unreadCount", unreadCount);
            model.addAttribute("likeNotice", messageMap);
        }

        // 查询关注类通知
        message = messageService.findLatestNotice(user.getId(), TOPIC_FOLLOW);
        if (message != null) {
            messageMap = new HashMap<>();
            messageMap.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());  // 反转，将转义字符还原
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);

            messageMap.put("user", userService.findUserById((Integer) data.get("userId")));
            messageMap.put("entityType", data.get("entityType"));
            messageMap.put("entityId", data.get("entityId"));

            int count = messageService.findNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageMap.put("count", count);

            int unreadCount = messageService.findUnderNoticeCount(user.getId(), TOPIC_FOLLOW);
            messageMap.put("unreadCount", unreadCount);
            model.addAttribute("followNotice", messageMap);
        }

        // 查询未读消息数量
        int letterUnreadCount = messageService.findUnreadLatterCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);

        // 查询未读通知的数量
        int noticeUnreadCount = messageService.findUnderNoticeCount(user.getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "/site/notice";
    }

    @RequestMapping(path = "/notice/detail/{topic}", method = RequestMethod.GET)
    public String getNoticeList(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.getUser();

        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(),topic));
        List<Message> list=messageService.findNoticeList(user.getId(),topic,page.getOffset(), page.getLimit());
        List<Map<String,Object>> noticeVoList=new ArrayList<>();

        if(list!=null){
            for (Message notice : list) {
                Map<String,Object> map=new HashMap<>();
                // 通知
                map.put("notice",notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());  // 反转，将转义字符还原
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));

                // 通知的作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVoList.add(map);
            }
        }
        model.addAttribute("notices", noticeVoList);

        // 设置已读
        System.out.println(list);
        List<Integer> ids = getLetterIds(list);
        System.out.println(ids);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }
}
