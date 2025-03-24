package org.n1vnhil.xhs.kv.biz.domain.repository;

import org.n1vnhil.xhs.kv.biz.domain.dataobject.NoteContentDO;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface NoteContentRepository extends CassandraRepository<NoteContentDO, UUID> {



}
