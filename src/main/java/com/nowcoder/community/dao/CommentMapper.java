package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 根据实体来查询
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    // 插入评论
    int insertComment(Comment comment);

    Comment selectCommentById(int id);
}
