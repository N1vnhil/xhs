package org.n1vnhil.framework.common.response;

import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> extends Response<List<T>> {

    /**
     * 当前页码
     */
    private long pageNo;

    /**
     * 总数据量
     */
    private long totalCount;

    /**
     * 每页数据量
     */
    private long pageSize;

    /**
     * 总页数
     */
    private long totalPage;

    public static <T> PageResponse<T> success(List<T> data, long pageNo, long totalCount) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(true);
        pageResponse.setData(data);
        pageResponse.setPageNo(pageNo);
        pageResponse.setTotalCount(totalCount);
        // 每页展示的数据量
        long pageSize = 10L;
        pageResponse.setPageSize(pageSize);
        // 计算总页数
        long totalPage = (totalCount + pageSize - 1) / pageSize;
        pageResponse.setTotalPage(totalPage);
        return pageResponse;
    }

    public static <T> PageResponse<T> success(List<T> data, long pageNo, long totalCount, long pageSize) {
        PageResponse<T> pageResponse = new PageResponse<>();
        pageResponse.setSuccess(true);
        pageResponse.setData(data);
        pageResponse.setPageNo(pageNo);
        pageResponse.setTotalCount(totalCount);
        pageResponse.setPageSize(pageSize);
        // 计算总页数
        long totalPage = pageSize == 0 ? 0 : (totalCount + pageSize - 1) / pageSize;
        pageResponse.setTotalPage(totalPage);
        return pageResponse;
    }

    public static long getTotalPages(long totalCount, long pageSize) {
        return pageSize == 0 ? 0 : (totalCount + pageSize - 1) / pageSize;
    }

    public static long getOffset(long pageNo, long pageSize) {
        if(pageNo < 1) pageNo = 1;
        return (pageNo - 1) * pageSize;
    }

}
