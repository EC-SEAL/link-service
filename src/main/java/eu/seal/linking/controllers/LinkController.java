package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.DataSet;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.User;
import eu.seal.linking.services.AuthService;
import eu.seal.linking.services.LinkService;
import eu.seal.linking.services.SessionUsersService;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link")
public class LinkController
{
    private final static Logger LOG = LoggerFactory.getLogger(LinkController.class);

    @Autowired
    private LinkService linkService;

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionUsersService sessionUsersService;

    @RequestMapping(value = "/request/submit", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public LinkRequest startLinkRequest(@RequestParam(required = true) String msToken, HttpServletRequest request)
            throws LinkAuthException, LinkApplicationException
    {
        String sessionId = authService.validateToken(msToken);
        String strLinkRequest = authService.getLinkRequestFromSession(sessionId);
        DataSet authentication = authService.getAuthenticationDataSet(sessionId);
        User user = sessionUsersService.getUser(authentication);

        LinkRequest linkRequest = linkService.storeNewRequest(strLinkRequest, user);

        return linkRequest;
    }

    @RequestMapping(value = "/{requestId}/status", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public Response getRequestStatus(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken, HttpServletRequest request)
    {
        return Response.ok().build();
    }
}
