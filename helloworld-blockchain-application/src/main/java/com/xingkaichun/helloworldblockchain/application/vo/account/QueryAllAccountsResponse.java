package com.xingkaichun.helloworldblockchain.application.vo.account;

import java.util.List;

/**
 *
 * @author 邢开春 409060350@qq.com
 */
public class QueryAllAccountsResponse {

    private long balance;
    private List<AccountVo2> accounts;


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

}
