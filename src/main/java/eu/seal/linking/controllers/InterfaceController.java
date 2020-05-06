package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.exceptions.UserNotAuthenticatedException;
import eu.seal.linking.model.DataSet;
import eu.seal.linking.services.AuthService;
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

    @GetMapping("main")
    public String getMainPage(HttpSession session) throws LinkAuthException
    {
        try
        {
            DataSet authentication = authService.getAuthenticationDataSet(SessionCommons.getSessionId(session, authService));
        } catch (UserNotAuthenticatedException e)
        {
            return "auth";
        }

        return "main";
    }

    @GetMapping("auth")
    public String getAuthPage(HttpSession session) throws LinkAuthException
    {
        try
        {
            DataSet authentication = authService.getAuthenticationDataSet(SessionCommons.getSessionId(session, authService));
        } catch (UserNotAuthenticatedException e)
        {
            return "auth";
        }

        return "main";
    }
}
