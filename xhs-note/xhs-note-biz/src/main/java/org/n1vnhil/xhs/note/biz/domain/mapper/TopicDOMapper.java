package org.n1vnhil.xhs.note.biz.domain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.n1vnhil.xhs.note.biz.domain.dataobject.TopicDO;

@Mapper
public interface TopicDOMapper {

    @Select("select * from t_topic where id=#{topicId}")
    TopicDO selectTopicById(Long topicId);

}
