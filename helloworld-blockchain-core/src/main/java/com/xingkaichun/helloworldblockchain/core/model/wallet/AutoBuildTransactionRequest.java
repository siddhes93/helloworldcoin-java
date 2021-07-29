package com.xingkaichun.helloworldblockchain.core.model.wallet;

import java.util.List;

/**
 * 构建交易请求
 *
 * @author 邢开春 409060350@qq.com
 */
public class AutoBuildTransactionRequest {

    //[非找零]收款方
    private List<Payee> nonChangePayees;


    //region get set
    public List<Payee> getNonChangePayees() {
        return nonChangePayees;
    }
    public void setNonChangePayees(List<Payee> nonChangePayees) {
        this.nonChangePayees = nonChangePayees;
    }
    //endregion
}
