package com.xingkaichun.helloworldblockchain.application.vo.framwork;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class PageCondition {

    private long from;

    private long size;

    public PageCondition() {
    }



    //region get set

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
    //endregion
}
