package org.n1vnhil.xhs.count.biz.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AggregationCountCollectUncollectNoteMqDTO {

    Long noteId;

    Long creatorId;

    Integer count;

}

