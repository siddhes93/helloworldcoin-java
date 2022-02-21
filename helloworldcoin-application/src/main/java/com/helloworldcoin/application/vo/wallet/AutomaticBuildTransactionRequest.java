package com.helloworldcoin.application.vo.wallet;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class AutomaticBuildTransactionRequest {

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
