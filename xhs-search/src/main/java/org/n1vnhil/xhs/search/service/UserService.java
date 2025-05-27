package org.n1vnhil.xhs.search.service;

import org.n1vnhil.framework.common.response.PageResponse;
import org.n1vnhil.xhs.search.model.vo.SearchUserReqVO;
import org.n1vnhil.xhs.search.model.vo.SearchUserRspVO;

public interface UserService {
    PageResponse<SearchUserRspVO> searchUserByKeywordAndPage(SearchUserReqVO searchUserReqVO);
}
