package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.User;
import eu.seal.linking.services.AuthService;
import eu.seal.linking.services.SessionUsersService;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("validator")
public class InterfaceController extends BaseController
{
    @Autowired
    AuthService authService;

    @Autowired
    SessionUsersService sessionUsersService;

    @RequestMapping("main")
    public String getMainPage(@CookieValue(name = SEAL_COOKIE, defaultValue = "") String sessionId)
    {
        String redirectPage = "main";

        try
        {
            User user = getUserFrom(sessionId);
        }
        catch (LinkApplicationException e)
        {
            redirectPage = "auth";
        }

        return redirectPage;
}

    @GetMapping("auth")
    //Just for test purposes, all logic in main page
    public String getAuthPage(HttpSession session) throws LinkAuthException
    {
        return "auth";
    }
}
