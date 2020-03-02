package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.model.FileObject;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.User;
import eu.seal.linking.services.LinkService;
import eu.seal.linking.services.SessionUsersService;
import eu.seal.linking.services.ValidatorService;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpSession;
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
public class ValidatorController
{
    private final static Logger LOG = LoggerFactory.getLogger(ValidatorController.class);

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private SessionUsersService sessionUsersService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public List<LinkRequest> getRequestList(@RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);

        return validatorService.getRequestsByDomain(user.getEntitlements());
    }

    @RequestMapping(value = "/{requestId}/lock", method = RequestMethod.GET)
    public Response lockRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);
        validatorService.lockRequest(requestId, user);
        return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/unlock", method = RequestMethod.GET)
    public Response unlockRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);
        validatorService.unlockRequest(requestId, user);
        return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/get", method = RequestMethod.GET)
    public LinkRequest getRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);
        return validatorService.getRequest(requestId, user);
    }

    @RequestMapping(value = "/{requestId}/approve", method = RequestMethod.GET)
    public Response approveRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);
        validatorService.approveRequest(requestId, user);
        return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/reject", method = RequestMethod.GET)
    public Response rejectRequest(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);
        validatorService.rejectRequest(requestId, user);
        return Response.ok().build();
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
}
