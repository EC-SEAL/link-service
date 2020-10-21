package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.IDLinkingException;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkAuthException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.exceptions.UserNotAuthenticatedException;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.StatusResponse;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.services.LinkService;
import eu.seal.linking.services.sm.SessionManagerConnService;

import java.net.URLEncoder;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("link")
public class LinkController extends BaseController
{
    private final static Logger LOG = LoggerFactory.getLogger(LinkController.class);

    @Autowired
    private LinkService linkService;

    @Autowired
    private SessionManagerConnService sessionManagerConnService;

    @Value("${linking.issuer}")
    private String linkIssuerId;

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

    @RequestMapping(value = "/{requestId}/result/get", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"})
    public ModelAndView getRequestResult(@PathVariable("requestId") String requestId, @RequestParam(required = true) String msToken, Model model)
            throws LinkAuthException, UserNotAuthenticatedException, LinkInternalException, IDLinkingException
    {
        String sessionId = authService.validateToken(msToken);
        String userId = getUserIdFrom(sessionId);

        String destUrl;
        String token = null;

         try
         {
            destUrl = (String) sessionManagerConnService.readVariable(sessionId, "ClientCallbackAddr");
         } catch (Exception e)
         {
            throw new IDLinkingException("Error retrieving ClientCallbackAddr from SM");
         }

         try {

            String requestStatus = linkService.getRequestStatus(requestId);
            LinkRequest linkRequest = linkService.getRequestResult(requestId, userId);

            if (requestStatus.equals(RequestStatus.ACCEPTED.toString()))
            {
                linkRequest.buildUriRepresentation(linkIssuerId);
                linkService.deleteRequest(requestId);

                authService.addLinkRequestToDataStore(sessionId, linkRequest);

                token = getRedirectToken(sessionId, destUrl, "OK", null);
            }
            else
            {
                if (requestStatus.equals(RequestStatus.REJECTED.toString()))
                {
                    linkService.deleteRequest(requestId);

                    authService.deleteLinkRequestFromDataStore(sessionId, linkRequest);
                }

                throw new LinkApplicationException("Request in " + requestStatus + " status");
            }
        }
        catch (LinkApplicationException e)
        {
            token = getRedirectToken(sessionId, destUrl, "ERROR", e.getMessage());
        }

        model.addAttribute("url", destUrl);
        model.addAttribute("token", token);

        return new ModelAndView("msToken");
    }

    private String getRedirectToken(String sessionId, String url, String status, String message)
            throws LinkInternalException
    {
        try
        {
            StatusResponse statusResponse = StatusResponse.build(status);
            statusResponse.setMessage(message);

            ObjectMapper objectMapper = new ObjectMapper();
            String token = sessionManagerConnService.generateToken(sessionId, null,// null);
                    URLEncoder.encode(objectMapper.writeValueAsString(statusResponse), "UTF-8"));

            return token;
        } catch (Exception e)
        {
            e.printStackTrace();

            throw new LinkInternalException();
        }
    }

}
