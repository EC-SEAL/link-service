package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.User;
import eu.seal.linking.model.common.DataSet;
import eu.seal.linking.services.AuthService;
import eu.seal.linking.services.SessionUsersService;
import eu.seal.linking.services.commons.SessionCommons;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("validator")
public class InterfaceController
{
    @Autowired
    AuthService authService;

    @Autowired
    SessionUsersService sessionUsersService;

    @RequestMapping("main")
    public String getMainPage(HttpSession session) throws LinkAuthException
    {
        String redirectPage = "main";

        User user = (User) session.getAttribute("user");
        if (user == null)
        {
            try
            {
                String sessionID = SessionCommons.getSessionId(session, authService);
                DataSet authentication = authService.getAuthenticationDataSet(sessionID);
                user = sessionUsersService.getUser(authentication);
                session.setAttribute("user", user);
                session.setAttribute("authSource", authService.getAuthSource(sessionID));
            } catch (LinkApplicationException |LinkAuthException e)
            {
                redirectPage = "auth";
            }
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
