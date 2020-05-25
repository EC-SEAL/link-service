package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.StatusResponse;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.services.LinkService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity startLinkRequest(@RequestParam(required = true) String msToken)
    {
        try
        {
            String sessionId = authService.validateToken(msToken);
            String strLinkRequest = authService.getLinkRequestFromSession(sessionId);
            String userId = getUserIdFrom(sessionId);

            LinkRequest linkRequest = linkService.storeNewRequest(strLinkRequest, userId);

            return ResponseEntity.ok(linkRequest);
        }
        catch (LinkAuthException | LinkApplicationException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{requestId}/status", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getRequestStatus(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
    {
        try
        {
            String requestStatus = linkService.getRequestStatus(requestId);

            return ResponseEntity.ok(StatusResponse.build(requestStatus));
        }
        catch (LinkApplicationException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{requestId}/cancel", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public ResponseEntity cancelRequest(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken)
    {
        try
        {
            String sessionId = authService.validateToken(msToken);
            String userId = getUserIdFrom(sessionId);

            linkService.cancelRequest(requestId, userId);
            return ResponseEntity.ok().build();
        }
        catch (LinkAuthException | LinkApplicationException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "/{requestId}/result/get", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public ResponseEntity getRequestResult(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken)
    {
        try
        {
            String sessionId = authService.validateToken(msToken);
            String userId = getUserIdFrom(sessionId);

            String requestStatus = linkService.getRequestStatus(requestId);

            if (requestStatus.equals(RequestStatus.ACCEPTED.toString()))
            {
                LinkRequest linkRequest = linkService.getRequestResult(requestId, userId);
                linkService.deleteRequest(requestId);
                return ResponseEntity.ok(linkRequest);
            }
            else if (requestStatus.equals(RequestStatus.REJECTED.toString()))
            {
                linkService.deleteRequest(requestId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        }
        catch (LinkAuthException | LinkApplicationException e)
        {
        }

        return ResponseEntity.notFound().build();
    }

}
