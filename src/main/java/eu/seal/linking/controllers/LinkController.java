package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.StatusResponse;
import eu.seal.linking.services.LinkService;

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
public class LinkController extends BaseController
{
    private final static Logger LOG = LoggerFactory.getLogger(LinkController.class);

    @Autowired
    private LinkService linkService;

    @RequestMapping(value = "/request/submit", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public LinkRequest startLinkRequest(@RequestParam(required = true) String msToken)
            throws LinkAuthException, LinkApplicationException
    {
        String sessionId = authService.validateToken(msToken);
        String strLinkRequest = authService.getLinkRequestFromSession(sessionId);
        String userId = getUserIdFrom(sessionId);

        LinkRequest linkRequest = linkService.storeNewRequest(strLinkRequest, userId);

        return linkRequest;
    }

    @RequestMapping(value = "/{requestId}/status", method = RequestMethod.GET, produces = "application/json")
    public StatusResponse getRequestStatus(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws LinkApplicationException
    {
        String requestStatus = linkService.getRequestStatus(requestId);

        return StatusResponse.build(requestStatus);
    }

    @RequestMapping(value = "/{requestId}/cancel", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public Response cancelRequest(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken)
            throws LinkApplicationException, LinkAuthException
    {
        String sessionId = authService.validateToken(msToken);
        String userId = getUserIdFrom(sessionId);

        linkService.cancelRequest(requestId, userId);

        return Response.ok().build();
    }

    // TODO: Get result, store in dataset and delete from database
    @RequestMapping(value = "/{requestId}/result/get", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public LinkRequest getRequestResult(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken)
            throws LinkApplicationException, LinkAuthException
    {
        String sessionId = authService.validateToken(msToken);
        String userId = getUserIdFrom(sessionId);

        LinkRequest linkRequest = linkService.getRequestResult(requestId, userId);

        //TODO: store object in dataset, and delete if all OK

        linkService.deleteRequest(requestId);

        return linkRequest;
    }

    //TODO: client just send id request (also files and messages)
}
