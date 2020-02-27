package eu.seal.linking.services;

import eu.seal.linking.dao.RequestMessageRepository;
import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.exceptions.RequestStatusException;
import eu.seal.linking.exceptions.UserNotAuthorizedException;
import eu.seal.linking.model.Message;
import eu.seal.linking.model.User;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestMessage;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.model.enums.UserMessageType;
import eu.seal.linking.services.commons.RequestCommons;
import eu.seal.linking.utils.CryptoUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MessagesService
{
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestMessageRepository requestMessageRepository;

    private final static Logger LOG = LoggerFactory.getLogger(MessagesService.class);

    public void storeMessage(String requestUid, String strMessage, User user, String recipient) throws LinkApplicationException
    {
        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        Message message = null;
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            message = objectMapper.readValue(strMessage, Message.class);
            message.setRecipientType(recipient);
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
            throw new RequestException("Message format is not valid.");
        }

        if (recipient.equals(UserMessageType.OFFICER.toString()))
        {
            checkRequesterFrom(request, user.getId());
            message.setSenderType(UserMessageType.REQUESTER.toString());
        }
        else
        {
            checkOfficerFrom(request, user.getId());
            message.setSenderType(UserMessageType.OFFICER.toString());
        }

        //TODO: validate user sender?
        message.validate();

        RequestMessage requestMessage = RequestCommons.getRequestMessageFrom(message, request);
        requestMessageRepository.save(requestMessage);
    }

    public List<Message> getConversation(String requestUid, User user) throws LinkApplicationException
    {
        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        try
        {
            checkRequesterFrom(request, user.getId());
        } catch (UserNotAuthorizedException e)
        {
            checkOfficerFrom(request, user.getId());
        }

        List<RequestMessage> requestMessages = request.getMessages();
        List<Message> messages = new ArrayList<Message>();
        for (RequestMessage requestMessage : requestMessages)
        {
            messages.add(RequestCommons.getMessageFrom(requestMessage));
        }

        return messages;
    }

    private void checkRequesterFrom(Request request, String requesterId) throws LinkApplicationException
    {
        try
        {
            if (!request.getRequesterId().equals(CryptoUtils.generateMd5(requesterId)))
            {
                throw new UserNotAuthorizedException();
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            LOG.error(e.getMessage(), e);
            throw new LinkInternalException(e.getMessage());
        }
    }

    private void checkOfficerFrom(Request request, String userId) throws LinkApplicationException
    {
        if (request.getAgentId() == null || !request.getAgentId().equals(userId))
        {
            throw new UserNotAuthorizedException();
        }

        if (!request.getStatus().equals(RequestStatus.LOCKED.toString()))
        {
            throw new RequestStatusException("Request have to be locked to send messages");
        }
    }

}
