package com.helloworldcoin.application.vo.wallet;

import java.util.List;

/**
 * 构建交易请求
 *
 * @author x.king xdotking@gmail.com
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
