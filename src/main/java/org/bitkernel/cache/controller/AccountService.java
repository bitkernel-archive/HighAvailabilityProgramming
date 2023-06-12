package org.bitkernel.cache.controller;

import org.bitkernel.cache.dao.AccountDao;
import org.bitkernel.cache.entity.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountDao accountDao;

    public AccountInfo getAccountInfo(long accountId) {
        return accountDao.getAccountInfo(accountId);
    }
}
