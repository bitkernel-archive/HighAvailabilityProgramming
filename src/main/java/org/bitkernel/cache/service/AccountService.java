package org.bitkernel.cache.service;

import io.bikcache.core.annotation.BikCacheable;
import org.bitkernel.cache.dao.AccountDao;
import org.bitkernel.cache.entity.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountDao accountDao;

    @BikCacheable(key = "'id_' + #accountId", cacheNames = "account_info")
    public AccountInfo getCachedAccountInfo(long accountId) {
        return accountDao.getAccountInfo(accountId);
    }
}
