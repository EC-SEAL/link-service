package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.model.AuthSource;
import eu.seal.linking.model.User;
import eu.seal.linking.model.module.RequestInfo;
import eu.seal.linking.model.module.UserAuthData;
import eu.seal.linking.services.ModuleService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link/module")
public class ModuleController extends BaseController
{
    @Autowired
    private ModuleService moduleService;

    @RequestMapping("user/data")
    public UserAuthData getUserAuthData(@CookieValue(name = SEAL_COOKIE, defaultValue = "") String sessionId)
            throws LinkApplicationException
    {
        User user = getUserFrom(sessionId);
        AuthSource authSource = authService.getAuthSource(sessionId);

        UserAuthData userAuthData = new UserAuthData(user, authSource);

        return userAuthData;
    }

    @RequestMapping("requests")
    public List<RequestInfo> getAgentRequests(@CookieValue(name = SEAL_COOKIE, defaultValue = "") String sessionId)
            throws LinkApplicationException
    {
        User user = getUserFrom(sessionId);

        return moduleService.getAgentRequests(user);
    }

    @RequestMapping("request/{requestId}/info")
    public RequestInfo getRequest(@CookieValue(name = SEAL_COOKIE, defaultValue = "") String sessionId, @PathVariable("requestId") String requestId)
            throws LinkApplicationException
    {
        User user = getUserFrom(sessionId);

        return moduleService.getRequestInfo(requestId, user);
    }

}
