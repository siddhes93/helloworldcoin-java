package com.xingkaichun.helloworldblockchain.application.vo.wallet;

import java.util.List;

/**
 * 构建交易请求
 *
 * @author 邢开春 409060350@qq.com
 */
public class AutomaticBuildTransactionRequest {

    //[非找零]收款方
    private List<PayeeVo> nonChangePayees;


    //region get set
    public List<PayeeVo> getNonChangePayees() {
        return nonChangePayees;
    }
    public void setNonChangePayees(List<PayeeVo> nonChangePayees) {
        this.nonChangePayees = nonChangePayees;
    }
    //endregion
}
