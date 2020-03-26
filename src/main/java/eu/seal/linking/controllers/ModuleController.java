package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.model.AuthSource;
import eu.seal.linking.model.User;
import eu.seal.linking.model.module.RequestInfo;
import eu.seal.linking.model.module.UserAuthData;
import eu.seal.linking.services.ModuleService;
import eu.seal.linking.services.SessionUsersService;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link/module")
public class ModuleController
{
    @Autowired
    private SessionUsersService sessionUsersService;

    @Autowired
    private ModuleService moduleService;

    @RequestMapping("user/data")
    public UserAuthData getUserAuthData(@RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);
        AuthSource authSource = getAuthSource(session);

        UserAuthData userAuthData = new UserAuthData(user, authSource);

        return userAuthData;
    }

    // Test function
    private User getSessionUser(HttpSession session) throws LinkApplicationException
    {
        User user = (User) session.getAttribute("user2");
        if (user == null)
        {
            user = sessionUsersService.getTestUser("ADMIN");
            session.setAttribute("user2", user);
        }

        return user;
    }

    // Test function
    private AuthSource getAuthSource(HttpSession session) throws LinkApplicationException
    {
        AuthSource authSource = (AuthSource) session.getAttribute("authSource2");
        if (authSource == null)
        {
            authSource = sessionUsersService.getTestAuthSource();
            session.setAttribute("authSource2", authSource);
        }

        return authSource;
    }

    @RequestMapping("requests")
    public List<RequestInfo> getAgentRequests(@RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);

        return moduleService.getAgentRequests(user);
    }

}
