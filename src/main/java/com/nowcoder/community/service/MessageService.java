package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter filter;

    // 查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    public List<Message> findConversations(int userId, int offset, int limit){
        return messageMapper.selectConversations(userId,offset,limit);
    }

    // 查询当前用户的会话数量
    public int findConversationRows(int userId){
        return messageMapper.selectConversationRows(userId);
    }

    // 查询每个会话的私信列表
    public List<Message> findLatters(String conversationId, int offset, int limit){
        return messageMapper.selectLetters(conversationId,offset,limit);
    }

    // 查询每个会话的私信数量
    public int findLatterCount(String conversationId){
        return messageMapper.selectLetterCount(conversationId);
    }

    // 查询未读消息数量
    public int findUnreadLatterCount(int userId, String conversationId){
        return messageMapper.selectUnreadLetterCount(userId,conversationId);
    }

    // 添加消息
    public int addMessage(Message message){
        if(message==null){
            throw new IllegalArgumentException("参数不能为空！");
        }

        // 转义HTML标记
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));

        // 过滤敏感词
        message.setContent(filter.filter(message.getContent()));

        return messageMapper.insertMessage(message);
    }

    // 读取消息
    public int readMessage(List<Integer> ids){
        return messageMapper.updateStatus(ids,1);
    }
}
