package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.IDLinkingException;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.model.Message;
import eu.seal.linking.model.User;
import eu.seal.linking.model.enums.UserMessageType;
import eu.seal.linking.services.MessagesService;

import java.util.List;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link")
public class MessagesController extends BaseController
{
    @Autowired
    private MessagesService messagesService;

    @RequestMapping(value = "/{requestId}/messages/send/{recipient:requester|officer}", method = RequestMethod.POST,
            consumes = {"application/x-www-form-urlencoded"}, produces = "application/json")
    public Response sendMessage(@PathVariable("requestId") String requestId, @PathVariable("recipient") String recipient,
                                @RequestParam String message, @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            User user = null;

            /*if (recipient.equals(UserMessageType.OFFICER.toString()))
            {
                user = getUserFromSessionToken(sessionToken);
            }
            else*/
            if (recipient.equals(UserMessageType.REQUESTER.toString()))
            {
                user = getUserFrom(sessionToken);
            }

            messagesService.storeMessage(requestId, message, user, recipient);

            return Response.ok().build();
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }

    @RequestMapping(value = "/{requestId}/messages/receive", produces = "application/json")
    public List<Message> getConversation(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken)
            throws IDLinkingException
    {
        try
        {
            return messagesService.getConversation(requestId);
        }
        catch (LinkApplicationException e)
        {
            throw new IDLinkingException(e.getMessage());
        }
    }
}
