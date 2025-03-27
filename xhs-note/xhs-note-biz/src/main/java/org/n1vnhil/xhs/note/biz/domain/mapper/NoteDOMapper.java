package org.n1vnhil.xhs.note.biz.domain.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.n1vnhil.xhs.note.biz.domain.dataobject.NoteDO;

@Mapper
public interface NoteDOMapper {

    @Insert("insert into t_note (id, title, content_empty, creator_id, topic_id, topic_name, top, type, img_uris, video_uri, visible, create_time, update_time, status, content_uuid) " +
            "VALUES (#{id}, #{title}, #{contentEmpty}, #{creatorId}, #{topicId}, #{topicName}, #{top}, #{type}, #{imgUris}, #{videoUri}, #{visible}, #{createTime}," +
            "#{updateTime}, #{status}, #{contentUuid})")
    void insert(NoteDO noteDO);

    NoteDO selectNoteById(Long id);

    int update(NoteDO noteDO);

    int updateTop(NoteDO noteDO);
}
