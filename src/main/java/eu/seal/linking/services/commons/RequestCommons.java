package eu.seal.linking.services.commons;

import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.exceptions.RequestNotFoundException;
import eu.seal.linking.exceptions.UserNotAuthorizedException;
import eu.seal.linking.model.FileObject;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.Message;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestFile;
import eu.seal.linking.model.db.RequestMessage;
import eu.seal.linking.utils.CryptoUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

public class RequestCommons
{
    private final static Logger LOG = LoggerFactory.getLogger(RequestCommons.class);

    public final static boolean REQ_ADD_ALL_FIELDS = true;

    public final static boolean REQ_NOT_ADD_ALL_FIELDS = false;

    private final static int NUM_EXPIRY_DAYS = -7;

    public static Request getRequestFrom(String uid, RequestRepository requestRepository) throws RequestNotFoundException
    {
        List<Request> requests = requestRepository.findByUid(uid);

        if (requests.size() == 0)
        {
            throw new RequestNotFoundException("Request " + uid + " not found");
        }

        return requests.get(0);
    }

    public static RequestFile getRequestFileFrom(FileObject fileObject, Request request)
    {
        RequestFile requestFile = new RequestFile();
        if (!Strings.isNullOrEmpty(fileObject.getFileID()))
        {
            requestFile.setId(new Long(fileObject.getFileID()));
        }
        requestFile.setName(fileObject.getFilename());
        requestFile.setMimeType(fileObject.getContentType());
        requestFile.setSize(fileObject.getFileSize());
        requestFile.setContent(fileObject.getContent());
        requestFile.setUploadDate(new Date());
        requestFile.setRequest(request);

        return requestFile;
    }

    public static FileObject getFileObjectFrom(RequestFile requestFile)
    {
        FileObject fileObject = new FileObject();
        fileObject.setFilename(requestFile.getName());
        fileObject.setFileID(requestFile.getId().toString()); //???
        fileObject.setContentType(requestFile.getMimeType());
        fileObject.setFileSize(requestFile.getSize());
        fileObject.setContent(requestFile.getContent());

        return fileObject;
    }

    public static RequestMessage getRequestMessageFrom(Message message, Request request)
    {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setDate(new Date(message.getTimestamp()));
        requestMessage.setSender(message.getSender());
        requestMessage.setSenderType(message.getSenderType());
        requestMessage.setMessage(message.getMessage());
        requestMessage.setRequest(request);

        return requestMessage;
    }

    public static Message getMessageFrom(RequestMessage requestMessage)
    {
        Message message = new Message();
        message.setTimestamp(requestMessage.getDate().getTime());
        message.setSender(requestMessage.getSender());
        message.setSenderType(requestMessage.getSenderType());
        message.setMessage(requestMessage.getMessage());
        return message;
    }

    public static LinkRequest getLinkRequestFrom(Request request, boolean addAllFields) throws RequestException
    {
        LinkRequest linkRequest = null;
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            linkRequest = objectMapper.readValue(request.getStrRequest(), LinkRequest.class);
            linkRequest.setId(request.getUid());
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
            throw new RequestException("Link request format is not valid.");
        }

        if (addAllFields)
        {
            List<FileObject> evidence = new ArrayList<FileObject>();
            for (RequestFile requestFile : request.getFiles())
            {
                evidence.add(RequestCommons.getFileObjectFrom(requestFile));
            }
            linkRequest.setEvidence(evidence);

            List<Message> conversation = new ArrayList<Message>();
            for (RequestMessage requestMessage : request.getMessages())
            {
                conversation.add(RequestCommons.getMessageFrom(requestMessage));
            }
            linkRequest.setConversation(conversation);
        }

        return linkRequest;
    }

    public static void checkRequesterFrom(Request request, String requesterId) throws LinkApplicationException
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

    public static void deleteExpiredRequests(RequestRepository requestRepository)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, NUM_EXPIRY_DAYS);
        Date expirationDate = calendar.getTime();

        List<Request> requests = requestRepository.getAllByLastUpdateBefore(expirationDate);
        for (Request request : requests)
        {
            requestRepository.delete(request);
        }
    }

    public static void updateRequestLastUpdate(Request request, RequestRepository requestRepository)
    {
        request.setLastUpdate(new Date());
        requestRepository.save(request);
    }
}
