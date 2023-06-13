package org.bitkernel.cache.controller;

import lombok.extern.slf4j.Slf4j;
import org.bitkernel.cache.entity.AccountInfo;
import org.bitkernel.cache.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("account")
@Slf4j
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("cache/id/{id}")
    public AccountInfo getCachedAccountById(@PathVariable long id) {
        return accountService.getCachedAccountInfo(id);
    }

    @GetMapping("cache/id2/{id}")
    public AccountInfo getCachedAccountById2(@PathVariable long id) {
        return accountService.getAccountInfoViaCallBackApi(id);
    }
}
