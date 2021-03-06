package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.IDLinkingException;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.User;
import eu.seal.linking.services.ValidatorService;

import java.util.List;

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
public class ValidatorController extends BaseController
{
    private final static Logger LOG = LoggerFactory.getLogger(ValidatorController.class);

    @Autowired
    private ValidatorService validatorService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public List<LinkRequest> getRequestList(@RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            User user = getUserFrom(sessionToken);

            return validatorService.getRequestsByDomain(user.getEntitlements());
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/lock", method = RequestMethod.GET)
    public Response lockRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            User user = getUserFrom(sessionToken);
            validatorService.lockRequest(requestId, user);
            return Response.ok().build();
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/unlock", method = RequestMethod.GET)
    public Response unlockRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            User user = getUserFrom(sessionToken);
            validatorService.unlockRequest(requestId, user);
            return Response.ok().build();
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/get", method = RequestMethod.GET)
    public LinkRequest getRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            User user = getUserFrom(sessionToken);
            return validatorService.getRequest(requestId, user);
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/approve", method = RequestMethod.GET)
    public Response approveRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            User user = getUserFrom(sessionToken);
            validatorService.approveRequest(requestId, user);
            return Response.ok().build();
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/reject", method = RequestMethod.GET)
    public Response rejectRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            User user = getUserFrom(sessionToken);
            validatorService.rejectRequest(requestId, user);
            return Response.ok().build();
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

}
