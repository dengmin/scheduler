package com.rose.scheduler.entity;

import java.util.List;

public class Pageable<T> {
    private int page;
    private int pageSize;
    private int total;
    private List<T> items;

    public Pageable(int page, int pageSize, int total, List<T> items){
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.items = items;
    }

    public Pageable(int page, int total, List<T> items){
        this(page, 20, total, items);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getPageTotal(){
        if(total % pageSize == 0){
            return total / pageSize;
        }else{
            return total / pageSize + 1;
        }
    }
}
