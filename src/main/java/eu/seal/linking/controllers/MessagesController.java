package eu.seal.linking.controllers;

import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.model.Message;
import eu.seal.linking.model.User;
import eu.seal.linking.model.enums.UserMessageType;
import eu.seal.linking.services.MessagesService;
import eu.seal.linking.services.SessionUsersService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("link")
public class MessagesController
{
    @Autowired
    private SessionUsersService sessionUsersService;

    @Autowired
    private MessagesService messagesService;

    // TODO: It will be a post
    @RequestMapping(value = "/{requestId}/messages/send/{recipient:requester|officer}", method = RequestMethod.GET)
    public Response sendMessage(@PathVariable("requestId") String requestId, @PathVariable("recipient") String recipient,
                                @RequestParam(required = false) String sessionToken, HttpSession session)
            throws LinkApplicationException, IOException
    {
        User user = null;
        String strMessage = null;

        if (recipient.equals(UserMessageType.OFFICER.toString()))
        {
            user = getSessionUser(session, "USER");

            // Test with local files
            ClassPathResource resource = new ClassPathResource("message-requester.json");
            strMessage = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        }
        else if (recipient.equals(UserMessageType.REQUESTER.toString()))
        {
            user = getSessionUser(session, "ADMIN");

            // Test with local files
            ClassPathResource resource = new ClassPathResource("message-officer.json");
            strMessage = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        }

        messagesService.storeMessage(requestId, strMessage, user, recipient);

        return Response.ok().build();
    }

    @RequestMapping(value = "/{requestId}/messages/receive", produces = "application/json")
    public List<Message> getConversation(@PathVariable("requestId") String requestId, @RequestParam(required = false) String sessionToken,
                                         HttpSession session) throws LinkApplicationException
    {
        User user = getSessionUser(session, "ADMIN");

        return messagesService.getConversation(requestId, user);
    }

    private User getSessionUser(HttpSession session, String userType) throws LinkApplicationException
    {
        User user = null;

        if (userType.equals("USER"))
        {
            user = (User) session.getAttribute("user");
        }
        else
        {
            user = (User) session.getAttribute("user2");
        }

        if (user == null)
        {
            user = sessionUsersService.getTestUser(userType);
            if (userType.equals("USER"))
            {
                session.setAttribute("user", user);
            }
            else
            {
                session.setAttribute("user2", user);
            }
        }

        return user;
    }
}
