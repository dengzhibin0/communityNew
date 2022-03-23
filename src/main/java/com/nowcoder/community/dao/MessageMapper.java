package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {

    // 查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    // 查询当前用户的会话数量
    int selectConversationRows(int userId);

    // 查询每个会话的私信列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    // 查询每个会话的私信数量
    int selectLetterCount(String conversationId);

    // 查询未读消息数量
    int selectUnreadLetterCount(int userId, String conversationId);

    // 添加私信
    int insertMessage(Message message);

    // 将未读设置为已读,删除
    int updateStatus(List<Integer> ids,int status);
}
