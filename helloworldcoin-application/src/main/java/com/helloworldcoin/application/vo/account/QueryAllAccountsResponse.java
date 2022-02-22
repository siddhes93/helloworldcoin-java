package com.helloworldcoin.application.vo.account;

import java.util.List;

/**
 *
 * @author x.king xdotking@gmail.com
 */
public class QueryAllAccountsResponse {

    private long balance;
    private List<AccountVo2> accounts;




    //region get set
    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public List<AccountVo2> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<AccountVo2> accounts) {
        this.accounts = accounts;
    }
    //endregion
}
