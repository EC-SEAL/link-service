package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.IDLinkingException;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.StatusResponse;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.services.LinkService;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${linking.issuer}")
    String linkIssuerId;

    @RequestMapping(value = "/request/submit", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public LinkRequest startLinkRequest(@RequestParam(required = true) String msToken)
            throws IDLinkingException
    {
        try
        {
            String sessionId = authService.validateToken(msToken);
            String strLinkRequest = authService.getLinkRequestFromSession(sessionId);
            String userId = getUserIdFrom(sessionId);

            LinkRequest linkRequest = linkService.storeNewRequest(strLinkRequest, userId);

            authService.addLinkRequestToDataStore(sessionId, linkRequest);

            return linkRequest;
        }
        catch (LinkApplicationException | LinkAuthException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/status", method = RequestMethod.GET, produces = "application/json")
    public StatusResponse getRequestStatus(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            String requestStatus = linkService.getRequestStatus(requestId);

            return StatusResponse.build(requestStatus);
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/cancel", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public Response cancelRequest(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken)
            throws IDLinkingException
    {
        try
        {
            String sessionId = authService.validateToken(msToken);
            String userId = getUserIdFrom(sessionId);

            LinkRequest linkRequest = linkService.getRequestResult(requestId, userId);

            linkService.cancelRequest(requestId, userId);

            authService.deleteLinkRequestFromDataStore(sessionId, linkRequest);

            return Response.ok().build();
        }
        catch (LinkApplicationException | LinkAuthException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/result/get", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public LinkRequest getRequestResult(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken)
            throws IDLinkingException
    {
        try
        {
            String sessionId = authService.validateToken(msToken);
            String userId = getUserIdFrom(sessionId);

            String requestStatus = linkService.getRequestStatus(requestId);
            LinkRequest linkRequest = linkService.getRequestResult(requestId, userId);

            if (requestStatus.equals(RequestStatus.ACCEPTED.toString()))
            {
                linkRequest.buildUriRepresentation(linkIssuerId);
                linkService.deleteRequest(requestId);

                authService.addLinkRequestToDataStore(sessionId, linkRequest);

                return linkRequest;
            }
            else if (requestStatus.equals(RequestStatus.REJECTED.toString()))
            {
                linkService.deleteRequest(requestId);

                authService.deleteLinkRequestFromDataStore(sessionId, linkRequest);
            }

            throw new IDLinkingException("Request in " + requestStatus + " status");
        }
        catch (LinkApplicationException | LinkAuthException e)
        {
            throw new IDLinkingException(e.getMessage());
        }


    }

}
