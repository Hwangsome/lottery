package com.clinic.lottery.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页码
     */
    private int page;

    /**
     * 每页数量
     */
    private int pageSize;

    /**
     * 数据列表
     */
    private List<T> list;

    public PageResult() {
    }

    public PageResult(long total, int page, int pageSize, List<T> list) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.list = list;
    }

    public static <T> PageResult<T> of(long total, int page, int pageSize, List<T> list) {
        return new PageResult<>(total, page, pageSize, list);
    }

    /**
     * 获取总页数
     */
    public int getTotalPages() {
        return pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }
}
