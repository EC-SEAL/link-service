package eu.seal.linking.services;

import eu.seal.linking.dao.RequestDomainRepository;
import eu.seal.linking.dao.RequestFileRepository;
import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.exceptions.RequestFileNotFoundException;
import eu.seal.linking.exceptions.RequestNotFoundException;
import eu.seal.linking.exceptions.UserNotAuthorizedException;
import eu.seal.linking.model.FileObject;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.User;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestDomain;
import eu.seal.linking.model.db.RequestFile;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.utils.CryptoUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

@Service
public class LinkService
{
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestDomainRepository requestDomainRepository;

    @Autowired
    private RequestFileRepository requestFileRepository;

    //public HttpResponse startLinkRequest();

    private final static Logger LOG = LoggerFactory.getLogger(LinkService.class);

    public LinkRequest storeNewRequest(String strRequest, User user) throws LinkApplicationException
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            LinkRequest linkRequest = objectMapper.readValue(strRequest, LinkRequest.class);
            Request request = initializeRequest(linkRequest, strRequest, user.getId());
            request = requestRepository.save(request);
            linkRequest.setId(request.getUid());
            return linkRequest;
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
            throw new RequestException("Link request format is not valid.");
        }
    }

    public List<Request> getRequestsByDomain(List<String> domains)
    {
        List<RequestDomain> requestDomains = requestDomainRepository.findByDomainIn(domains);
        return requestRepository.findByDomainsIn(requestDomains);
    }

    public String getRequestStatus(String uid, User user) throws LinkApplicationException
    {
        Request request = getRequestFrom(uid);
        checkRequesterFrom(request, user.getId());

        return request.getStatus();
    }

    public void cancelRequest(String uid, User user) throws LinkApplicationException
    {
        Request request = getRequestFrom(uid);
        checkRequesterFrom(request, user.getId());
        requestRepository.delete(request);
    }

    public void storeFileRequest(String requestUid, String strFile, User user) throws LinkApplicationException
    {
        Request request = getRequestFrom(requestUid);
        checkRequesterFrom(request, user.getId());

        FileObject fileObject = null;
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            fileObject = objectMapper.readValue(strFile, FileObject.class);
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
            throw new RequestException("File object format is not valid.");
        }

        RequestFile requestFile = null;
        if (Strings.isNullOrEmpty(fileObject.getFileID()))
        {
            requestFile = getRequestFileFrom(fileObject, request);
        }
        else
        {
            requestFile = requestFileRepository.findById(new Long(fileObject.getFileID())).orElseThrow(() -> new RequestFileNotFoundException());

            if (!requestFile.getRequest().getUid().equals(request.getUid()))
            {
                throw new RequestNotFoundException();
            }

            updateRequestFileFrom(requestFile, fileObject);
        }

        requestFileRepository.save(requestFile);
    }


    private static Request initializeRequest(LinkRequest linkRequest, String strRequest, String requesterId)
            throws LinkInternalException
    {
        Date currentDate = new Date();

        Request request = new Request();
        request.setUid(UUID.randomUUID().toString());
        try
        {
            request.setRequesterId(CryptoUtils.generateMd5(requesterId));
            request.setOwnerId(CryptoUtils.generateMd5(linkRequest.getIssuer()));
        }
        catch (NoSuchAlgorithmException e)
        {
            LOG.error(e.getMessage(), e);
            throw new LinkInternalException(e.getMessage());
        }
        request.setEntryDate(currentDate);

        JsonStringEncoder encoder = JsonStringEncoder.getInstance();
        request.setStrRequest(new String(encoder.quoteAsString(strRequest)));

        request.setLastUpdate(currentDate);
        request.setStatus(RequestStatus.PENDING.toString());

        List<RequestDomain> domains = new ArrayList<RequestDomain>();
        domains.add(new RequestDomain(linkRequest.getaSubjectIssuer(), request));
        domains.add(new RequestDomain(linkRequest.getbSubjectIssuer(), request));
        request.setDomains(domains);

        return request;
    }

    private Request getRequestFrom(String uid) throws RequestNotFoundException
    {
        List<Request> requests = requestRepository.findByUid(uid);

        if (requests.size() == 0)
        {
            throw new RequestNotFoundException("Request " + uid + " not found");
        }

        return requests.get(0);
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

    private RequestFile getRequestFileFrom(FileObject fileObject, Request request)
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

    private void updateRequestFileFrom(RequestFile requestFile, FileObject fileObject)
    {
        requestFile.setName(fileObject.getFilename());
        requestFile.setMimeType(fileObject.getContentType());
        requestFile.setSize(fileObject.getFileSize());
        requestFile.setContent(fileObject.getContent());
        requestFile.setUploadDate(new Date());
    }
}
