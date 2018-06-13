package cn.sf.bean.beans.page;

import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@ToString
public class PageParam implements Serializable {
    @Setter
    private Integer pageNo;
    @Setter
    private Integer pageSize;
    @Setter
    private Integer offset;
    @Setter
    private Integer limit;
    private int defaultPageNo = 1; /*页码默认从1开始*/
    private int defaultPageSize = 10;

    private PageParam() {}

    public static PageParam valueOfByPageNo(Integer pageNo, Integer pageSize) {
        return new PageParam(pageNo, pageSize, 1);
    }
    public static PageParam valueOfByPageNo(Integer pageNo, Integer pageSize, Integer defaultPageSize) {
        PageParam pageInfo = new PageParam(pageNo, pageSize, 1);
        pageInfo.defaultPageSize = defaultPageSize;
        return pageInfo;
    }

    public static PageParam valueOfByOffset(Integer offset, Integer limit) {
        return new PageParam(offset, limit, 2);
    }
    public static PageParam valueOfByOffset(Integer offset, Integer limit, Integer defaultPageSize) {
        PageParam pageInfo = new PageParam(offset, limit, 2);
        pageInfo.defaultPageSize = defaultPageSize;
        return pageInfo;
    }

    private PageParam(Integer _arg1, Integer _arg2, Integer type) {
        if (type == 1) {
            this.pageNo = _arg1;
            this.pageSize = _arg2;
        }
        if (type == 2) {
            this.offset = _arg1;
            this.limit = _arg2;
        }

    }

    public Integer getPageNo() {
        if (null == pageNo || pageNo <= 0) {
            pageNo = defaultPageNo;
        }
        return pageNo;
    }

    public Integer getPageSize() {
        if (null == pageSize || pageSize <= 0) {
            pageSize = defaultPageSize;
        }
        return pageSize;
    }

    public Integer getOffset() {
        if (null == offset || offset <= 0) {
            return (getPageNo() - 1) * getPageSize();
        }
        return offset;
    }

    public Integer getLimit() {
        if (null == limit || limit <= 0) {
            return getPageSize();
        }
        return limit;
    }
}