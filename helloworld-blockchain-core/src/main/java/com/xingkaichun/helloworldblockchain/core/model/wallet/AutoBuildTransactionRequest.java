package com.xingkaichun.helloworldblockchain.core.model.wallet;

import java.util.List;

/**
 * 构建交易请求
 *
 * @author 邢开春 409060350@qq.com
 */
public class AutoBuildTransactionRequest {

    //收款方
    private List<Payee> payees;


    //region get set
    public List<Payee> getPayees() {
        return payees;
    }
    public void setPayees(List<Payee> payees) {
        this.payees = payees;
    }
    //endregion
}
