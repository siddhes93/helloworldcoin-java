package com.helloworldcoin.core.model.wallet;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class AutoBuildTransactionRequest {

    //Non-change Payee
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
