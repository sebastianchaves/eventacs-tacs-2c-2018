package com.eventacs.account.controller;

import com.eventacs.account.service.AccountService;
import com.eventacs.account.dto.UserAccountDTO;
import com.eventacs.user.dto.UserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/eventacs")
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseBody
    public UserInfoDTO signup(@RequestBody UserAccountDTO userAccountDTO) {
        LOGGER.info("/eventacs/signup [POST]");
        return this.accountService.createUser(userAccountDTO);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public UserInfoDTO login(@RequestBody UserAccountDTO userAccountDTO) {
        LOGGER.info("/eventacs/login [POST]");
        return this.accountService.login(userAccountDTO);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public UserInfoDTO logout(@RequestBody String sessionCookieId) {
        LOGGER.info("/eventacs/logout [POST]");
        return this.accountService.logout(sessionCookieId);
    }

}
