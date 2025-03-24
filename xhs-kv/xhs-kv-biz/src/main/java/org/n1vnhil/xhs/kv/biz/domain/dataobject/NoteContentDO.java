package org.n1vnhil.xhs.kv.biz.domain.dataobject;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Data
@AllArgsConstructor
@NotBlank
@Builder
@Table("note_content")
public class NoteContentDO {

    @PrimaryKey("id")
    private UUID id;

    private String content;

}
