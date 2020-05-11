package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.DataSet;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.StatusResponse;
import eu.seal.linking.model.User;
import eu.seal.linking.services.AuthService;
import eu.seal.linking.services.LinkService;
import eu.seal.linking.services.SessionUsersService;

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
    public LinkRequest startLinkRequest(@RequestParam(required = true) String msToken)
            throws LinkAuthException, LinkApplicationException
    {
        String sessionId = authService.validateToken(msToken);
        String strLinkRequest = authService.getLinkRequestFromSession(sessionId);
        DataSet authentication = authService.getAuthenticationDataSet(sessionId);
        User user = sessionUsersService.getUser(authentication);

        LinkRequest linkRequest = linkService.storeNewRequest(strLinkRequest, user);

        return linkRequest;
    }

    @RequestMapping(value = "/{requestId}/status", method = RequestMethod.GET, produces = "application/json")
    public StatusResponse getRequestStatus(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws LinkApplicationException
    {
        DataSet authentication = authService.getAuthenticationDataSet(sessionToken);
        User user = sessionUsersService.getUser(authentication);

        String requestStatus = linkService.getRequestStatus(requestId, user);

        return StatusResponse.build(requestStatus);
    }

    @RequestMapping(value = "/{requestId}/cancel", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public Response cancelRequest(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken)
            throws LinkApplicationException, LinkAuthException
    {
        String sessionId = authService.validateToken(msToken);
        DataSet authentication = authService.getAuthenticationDataSet(sessionId);
        User user = sessionUsersService.getUser(authentication);

        linkService.cancelRequest(requestId, user);

        return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/result/get", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public LinkRequest getRequestResult(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken)
            throws LinkApplicationException, LinkAuthException
    {
        String sessionId = authService.validateToken(msToken);
        DataSet authentication = authService.getAuthenticationDataSet(sessionId);
        User user = sessionUsersService.getUser(authentication);

        return linkService.getRequestResult(requestId, user);
    }
}
