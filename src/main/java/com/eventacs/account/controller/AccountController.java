package com.eventacs.account.controller;

import com.eventacs.account.dto.UserLoginDTO;
import com.eventacs.account.service.AccountService;
import com.eventacs.account.dto.UserAccountDTO;
import com.eventacs.external.eventbrite.model.GetAccessToken;
import com.eventacs.user.dto.UserInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/eventacs")
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseBody
    public boolean signup(@RequestBody UserAccountDTO userAccountDTO) {
        LOGGER.info("/eventacs/signup [POST] Username: {} Email: {} FullName: {}",
                userAccountDTO.getUserName(),
                userAccountDTO.getFullName(),
                userAccountDTO.getEmail());
//        boolean res = this.accountService.createUser(userAccountDTO);
//        return "{\"isUserCreated\":" + res + "}";
        return this.accountService.createUser(userAccountDTO);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public GetAccessToken login(@RequestBody UserLoginDTO userLoginDTO) {
        LOGGER.info("/eventacs/login [POST] Username: {}", userLoginDTO.getUsername());
        return this.accountService.login(userLoginDTO);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public void logout(@RequestHeader("Authorization") String token) {
        LOGGER.info("/eventacs/logout [POST]");
        this.accountService.logout(token.replace("bearer ","")
                .replace("Bearer ",""));
    }

}
