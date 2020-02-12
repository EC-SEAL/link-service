package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.RequestNotFoundException;
import eu.seal.linking.model.DataSet;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.StatusResponse;
import eu.seal.linking.model.User;
import eu.seal.linking.model.UserCM;
import eu.seal.linking.services.LinkService;
import eu.seal.linking.services.SessionUsersService;
import eu.seal.linking.services.UsersCMService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test/link")
public class LinkControllerTest
{
    private final static Logger LOG = LoggerFactory.getLogger(LinkController.class);

    @Autowired
    private LinkService linkService;

    @Autowired
    private SessionUsersService sessionUsersService;

    /*@GetMapping
    @RequestMapping(value = "/init", method = RequestMethod.GET, produces = "application/json")
    public Response initSessionParams(HttpSession session) throws LinkApplicationException, IOException
    {
        if (session.getAttribute("user") == null)
        {
            User user = sessionUsersService.getTestUser();
            session.setAttribute("user", user);
        }

        return Response.ok().build();
    }*/

    @RequestMapping(value = "/request/submit", method = RequestMethod.GET, produces = "application/json")
    public LinkRequest startLinkRequest(@RequestParam(required = true) String msToken, HttpSession session)
            throws LinkApplicationException, IOException
    {
        User user = getSessionUser(session);

        // Test with local file
        ClassPathResource resource = new ClassPathResource("request.json");
        String strRequest = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);

        LinkRequest linkRequest = linkService.storeNewRequest(strRequest, user);

        return linkRequest;
    }

    @RequestMapping(value = "/{requestId}/status", method = RequestMethod.GET, produces = "application/json")
    public StatusResponse getRequestStatus(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);

        String requestStatus = linkService.getRequestStatus(requestId, user);

        return StatusResponse.build(requestStatus);
    }

    @RequestMapping(value = "/{requestId}/cancel", method = RequestMethod.GET)
    public Response cancelRequest(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken, HttpSession session)
            throws LinkApplicationException
    {
        User user = getSessionUser(session);

        linkService.cancelRequest(requestId, user);

        return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/files/upload", method = RequestMethod.GET)
    // TODO: file param
    public Response uploadFile(@PathVariable("requestId") String requestId, @RequestParam String msToken,
                               @RequestParam(required = false) String update, HttpSession session)
            throws LinkApplicationException, IOException
    {
        User user = getSessionUser(session);

        // Test with local files
        String file = (update == null) ? "file.json" : "file2.json";
        ClassPathResource resource = new ClassPathResource(file);
        String strFile = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);

        linkService.storeFileRequest(requestId, strFile, user);

        return Response.ok().build();
    }

    // Admin services???
    /*@RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public List<Request> getRequestList(@RequestParam(required = true) String msToken, HttpSession session)
    {
        User user = (User) session.getAttribute("user");

        return linkService.getRequestsByDomain(user.getEntitlements());
    }

    @RequestMapping(value = "/{requestId}/lock", method = RequestMethod.GET)
    public Response lockRequest(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken, HttpServletRequest request)
    {

        return Response.ok().build();
    }*/

    private User getSessionUser(HttpSession session) throws LinkApplicationException
    {
        User user = (User) session.getAttribute("user");
        if (user == null)
        {
            user = sessionUsersService.getTestUser();
            session.setAttribute("user", user);
        }

        return user;
    }
}
