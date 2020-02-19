package eu.seal.linking.services.commons;

import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.exceptions.RequestNotFoundException;
import eu.seal.linking.model.FileObject;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.Message;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestFile;
import eu.seal.linking.model.db.RequestMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

public class RequestCommons
{
    private final static Logger LOG = LoggerFactory.getLogger(RequestCommons.class);

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

    public static LinkRequest getLinkRequestFrom(Request request) throws RequestException
    {
        LinkRequest linkRequest = null;
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            linkRequest = objectMapper.readValue(request.getStrRequest(), LinkRequest.class);
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
            throw new RequestException("Link request format is not valid.");
        }

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

        return linkRequest;
    }
}
