package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.UserNotAuthenticatedException;
import eu.seal.linking.model.DataSet;
import eu.seal.linking.model.User;
import eu.seal.linking.services.AuthService;
import eu.seal.linking.services.SessionUsersService;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController
{
    @Autowired
    protected AuthService authService;

    @Autowired
    protected SessionUsersService sessionUsersService;

    protected User getSessionUser(HttpSession session) throws LinkApplicationException
    {
        User user = (User) session.getAttribute("user");

        if (user == null)
        {
            throw new UserNotAuthenticatedException();
        }

        return user;
    }

    protected User getUserFromSessionToken(String sessionToken) throws LinkApplicationException
    {
        DataSet authentication = authService.getAuthenticationDataSet(sessionToken);
        User user = sessionUsersService.getUser(authentication);

        return user;
    }

}
