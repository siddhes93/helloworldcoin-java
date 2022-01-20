package com.helloworldcoin.application.vo.framwork;

/**
 *
 * @author x.king xdotking@gmail.com
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
