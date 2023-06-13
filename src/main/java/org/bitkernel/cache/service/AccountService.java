package org.bitkernel.cache.service;

import com.sun.istack.NotNull;
import io.bikcache.core.BikCacheApi;
import io.bikcache.core.annotation.BikCacheable;
import lombok.extern.slf4j.Slf4j;
import org.bitkernel.cache.dao.AccountDao;
import org.bitkernel.cache.entity.AccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService {
    @Autowired
    private AccountDao accountDao;

    @BikCacheable(key = "'id_' + #accountId", cacheNames = "account_info")
    public AccountInfo getCachedAccountInfo(long accountId) {
        return accountDao.getAccountInfo(accountId);
    }

    @NotNull
    public AccountInfo getAccountInfoViaCallBackApi(long accountId) {
        Object obj = BikCacheApi.get("account_info", "'id_' + #arg0", "def account_info",
                accountDao, "getAccountInfo", new Class[]{long.class}, accountId);
        return (AccountInfo) obj;
    }
}
