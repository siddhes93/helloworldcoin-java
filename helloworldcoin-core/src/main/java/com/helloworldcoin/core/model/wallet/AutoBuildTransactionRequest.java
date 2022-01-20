package com.helloworldcoin.core.model.wallet;

import java.util.List;

/**
 * 构建交易请求
 *
 * @author x.king xdotking@gmail.com
 */
//TODO 重命名
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
