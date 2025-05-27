package org.n1vnhil.xhs.search.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.framework.common.util.NumberUtils;
import org.n1vnhil.xhs.search.index.UserIndex;
import org.n1vnhil.xhs.search.model.vo.SearchUserReqVO;
import org.n1vnhil.xhs.search.model.vo.SearchUserRspVO;
import org.n1vnhil.xhs.search.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public PageResponse<SearchUserRspVO> searchUserByKeywordAndPage(SearchUserReqVO searchUserReqVO) {
        String keyword = searchUserReqVO.getKeyword();
        Integer pageNo = searchUserReqVO.getPageNo();
        SearchRequest searchRequest = new SearchRequest(UserIndex.NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, UserIndex.FIELD_USER_NICKNAME, UserIndex.FIELD_USER_XHS_ID));
        SortBuilder<?> sortBuilder = new FieldSortBuilder(UserIndex.FIELD_USER_FANS_TOTAL).order(SortOrder.DESC);
        searchSourceBuilder.sort(sortBuilder);

        int pageSize = 10, from = (pageNo - 1) * pageSize;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(pageSize);

        HighlightBuilder highlightBuilder =  new HighlightBuilder();
        highlightBuilder.field(UserIndex.FIELD_USER_NICKNAME)
                    .preTags("<strong>")
                    .postTags("</strong>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        List<SearchUserRspVO> res = null;
        long total = 0;
        try {
            log.info("==> SearchRequest: {}", searchRequest);
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            total = searchResponse.getHits().getTotalHits().value;
            log.info("==> 命中文档数：{}", total);
            res = Lists.newArrayList();
            SearchHits searchHits = searchResponse.getHits();
            for(SearchHit hit: searchHits) {
                log.info("==> 文档数据：{}", hit.getSourceAsString());
                Map<String, Object> map = hit.getSourceAsMap();
                Long userId = ((Number) map.get(UserIndex.FIELD_USER_ID)).longValue();
                String nickname = (String) map.get(UserIndex.FIELD_USER_NICKNAME);
                String avatar = (String) map.get(UserIndex.FIELD_USER_AVATAR);
                String xhsId = (String) map.get(UserIndex.FIELD_USER_XHS_ID);
                Integer noteTotal = ((Number) map.get(UserIndex.FIELD_USER_NOTE_TOTAL)).intValue();
                Integer fansTotal = ((Number) map.get(UserIndex.FIELD_USER_FANS_TOTAL)).intValue();
                String highlightNickname = null;
                if(CollUtil.isNotEmpty(hit.getHighlightFields())
                        && hit.getHighlightFields().containsKey(UserIndex.FIELD_USER_NICKNAME)) {
                    highlightNickname = hit.getHighlightFields().get(UserIndex.FIELD_USER_NICKNAME).fragments()[0].string();
                }


                SearchUserRspVO searchUserRspVO = SearchUserRspVO.builder()
                        .userId(userId)
                        .nickname(nickname)
                        .xhsId(xhsId)
                        .avatar(avatar)
                        .fansTotal(NumberUtils.formatNumberString(fansTotal))
                        .noteTotal(noteTotal)
                        .highlightNickname(highlightNickname)
                        .build();

                res.add(searchUserRspVO);
            }
        } catch (Exception e) {
            log.error("==> 查询ES异常：", e);
        }

        return PageResponse.success(res, pageNo, total);
    }
}
