package com.xingkaichun.helloworldblockchain.explorer.dto.node;

/**
 *
 * @author 邢开春
 */
public class SetAutoSearchNodeRequest {

    private boolean autoSearchNode;




    //region get set
    public boolean isAutoSearchNode() {
        return autoSearchNode;
    }

    public void setAutoSearchNode(boolean autoSearchNode) {
        this.autoSearchNode = autoSearchNode;
    }
    //endregion
}
