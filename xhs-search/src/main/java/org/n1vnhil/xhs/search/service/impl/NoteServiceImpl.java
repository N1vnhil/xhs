package org.n1vnhil.xhs.search.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FieldValueFactorFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FieldValueFactorFunctionBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.n1vnhil.framework.common.constant.DateConstants;
import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.framework.common.util.NumberUtils;
import org.n1vnhil.xhs.search.index.NoteIndex;
import org.n1vnhil.xhs.search.model.vo.SearchNoteReqVO;
import org.n1vnhil.xhs.search.model.vo.SearchNoteRspVO;
import org.n1vnhil.xhs.search.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NoteServiceImpl implements NoteService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public PageResponse<SearchNoteRspVO> searchNote(SearchNoteReqVO searchNoteReqVO) {

        String keyword = searchNoteReqVO.getKeyword();

        Integer pageNo = searchNoteReqVO.getPageNo();

        SearchRequest searchRequest = new SearchRequest(NoteIndex.NAME);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(keyword)
                .field(NoteIndex.FIELD_NOTE_TITLE, 2)
                .field(NoteIndex.FIELD_NOTE_TOPIC);

        // 创建 FilterFunctionBuilder 数组
        // "functions": [
        //         {
        //           "field_value_factor": {
        //             "field": "like_total",
        //             "factor": 0.5,
        //             "modifier": "sqrt",
        //             "missing": 0
        //           }
        //         },
        //         {
        //           "field_value_factor": {
        //             "field": "collect_total",
        //             "factor": 0.3,
        //             "modifier": "sqrt",
        //             "missing": 0
        //           }
        //         },
        //         {
        //           "field_value_factor": {
        //             "field": "comment_total",
        //             "factor": 0.2,
        //             "modifier": "sqrt",
        //             "missing": 0
        //           }
        //         }
        //       ],
        FunctionScoreQueryBuilder.FilterFunctionBuilder[] filterFunctionBuilders = new FunctionScoreQueryBuilder.FilterFunctionBuilder[] {
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        new FieldValueFactorFunctionBuilder(NoteIndex.FIELD_NOTE_LIKE_TOTAL)
                                .factor(0.5f)
                                .modifier(FieldValueFactorFunction.Modifier.SQRT)
                                .missing(0)
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        new FieldValueFactorFunctionBuilder(NoteIndex.FIELD_NOTE_COLLECT_TOTAL)
                                .factor(0.3f)
                                .modifier(FieldValueFactorFunction.Modifier.SQRT)
                                .missing(0)
                ),
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        new FieldValueFactorFunctionBuilder(NoteIndex.FIELD_NOTE_COMMENT_TOTAL)
                                .factor(0.2f)
                                .modifier(FieldValueFactorFunction.Modifier.SQRT)
                                .missing(0)
                )
        };

        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery(queryBuilder, filterFunctionBuilders)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
                .boostMode(CombineFunction.SUM);
        sourceBuilder.query(functionScoreQueryBuilder);
        sourceBuilder.sort(new FieldSortBuilder("_score").order(SortOrder.DESC));

        int pageSize = 10;
        int from = (pageNo - 1) * pageSize;
        sourceBuilder.from(from);
        sourceBuilder.size(pageSize);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field(NoteIndex.FIELD_NOTE_TITLE)
                .preTags("<strong>")
                .postTags("</strong>");
        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);

        List<SearchNoteRspVO> searchNoteRspVOS = null;
        long total = 0;
        try {
            log.info("==> SearchRequest: {}", searchRequest);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            total = searchResponse.getHits().getTotalHits().value;
            log.info("==> 命中文档总数：{}", total);
            searchNoteRspVOS = Lists.newArrayList();

            SearchHits hits = searchResponse.getHits();
            for(SearchHit hit : hits) {
                log.info("==> 文档数据：{}", hit.getSourceAsMap());
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Long noteId = (Long) sourceAsMap.get(NoteIndex.FIELD_NOTE_ID);
                String cover = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_COVER);
                String title = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_TITLE);
                String avatar = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_AVATAR);
                String nickname = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_NICKNAME);
                // 获取更新时间
                String updateTimeStr = (String) sourceAsMap.get(NoteIndex.FIELD_NOTE_UPDATE_TIME);
                LocalDateTime updateTime = LocalDateTime.parse(updateTimeStr, DateConstants.DATE_FORMAT_Y_M_D_H_M_S);
                Integer likeTotal = (Integer) sourceAsMap.get(NoteIndex.FIELD_NOTE_LIKE_TOTAL);

                // 获取高亮字段
                String highlightedTitle = null;
                if (CollUtil.isNotEmpty(hit.getHighlightFields())
                        && hit.getHighlightFields().containsKey(NoteIndex.FIELD_NOTE_TITLE)) {
                    highlightedTitle = hit.getHighlightFields().get(NoteIndex.FIELD_NOTE_TITLE).fragments()[0].string();
                }

                // 构建 VO 实体类
                SearchNoteRspVO searchNoteRspVO = SearchNoteRspVO.builder()
                        .noteId(noteId)
                        .cover(cover)
                        .title(title)
                        .highlightTitle(highlightedTitle)
                        .avatar(avatar)
                        .nickname(nickname)
                        .updateTime(updateTime)
                        .likeTotal(NumberUtils.formatNumberString(likeTotal))
                        .build();
                searchNoteRspVOS.add(searchNoteRspVO);

            }

        } catch (Exception e) {
            log.error("查询 ElasticSearch 异常：", e);
        }

        return PageResponse.success(searchNoteRspVOS, pageNo, pageSize);
    }
}
