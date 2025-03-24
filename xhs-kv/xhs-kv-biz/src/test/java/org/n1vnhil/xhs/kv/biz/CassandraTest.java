package org.n1vnhil.xhs.kv.biz;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.n1vnhil.xhs.kv.biz.domain.dataobject.NoteContentDO;
import org.n1vnhil.xhs.kv.biz.domain.repository.NoteContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class CassandraTest {

    @Resource
    private NoteContentRepository noteContentRepository;

    @Test
    public void testInsert() {
        NoteContentDO noteContentDO = NoteContentDO.builder()
                .id(UUID.randomUUID())
                .content("Test Cassandra")
                .build();

        noteContentRepository.save(noteContentDO);
    }

}
