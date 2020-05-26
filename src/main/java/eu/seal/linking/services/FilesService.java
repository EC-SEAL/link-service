package eu.seal.linking.services;

import eu.seal.linking.dao.RequestFileRepository;
import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.exceptions.RequestFileNotFoundException;
import eu.seal.linking.exceptions.RequestNotFoundException;
import eu.seal.linking.exceptions.RequestStatusException;
import eu.seal.linking.exceptions.UserNotAuthorizedException;
import eu.seal.linking.model.FileObject;
import eu.seal.linking.model.User;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestFile;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.services.commons.RequestCommons;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

@Service
public class FilesService
{
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestFileRepository requestFileRepository;

    private final static Logger LOG = LoggerFactory.getLogger(LinkService.class);

    public void storeFileRequest(String requestUid, String strFile/*, User user*/) throws LinkApplicationException
    {
        RequestCommons.deleteExpiredRequests(requestRepository);

        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        //RequestCommons.checkRequesterFrom(request, user.getId());

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
            requestFile = RequestCommons.getRequestFileFrom(fileObject, request);
        }
        else
        {
            requestFile = requestFileRepository.findById(new Long(fileObject.getFileID())).orElseThrow(() ->
                    new RequestFileNotFoundException("File for request " + requestUid + " not found"));

            if (!requestFile.getRequest().getUid().equals(request.getUid()))
            {
                throw new RequestNotFoundException("Request " + requestUid + " not found");
            }

            updateRequestFileFrom(requestFile, fileObject);
        }

        requestFileRepository.save(requestFile);

        RequestCommons.updateRequestLastUpdate(request, requestRepository);
    }

    public List<FileObject> getFilesFromRequest(String requestUid, User user)
            throws LinkApplicationException
    {
        RequestCommons.deleteExpiredRequests(requestRepository);

        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        if (request.getAgentId() == null || !request.getAgentId().equals(user.getId()))
        {
            throw new UserNotAuthorizedException();
        }

        if (!request.getStatus().equals(RequestStatus.LOCKED.toString()))
        {
            throw new RequestStatusException("Request have to be locked to download its files");
        }

        List<FileObject> files = new ArrayList<FileObject>();
        for (RequestFile requestFile : request.getFiles())
        {
            files.add(RequestCommons.getFileObjectFrom(requestFile));
        }

        return files;
    }

    public FileObject getFileFromRequest(String requestUid, Long fileId, User user)
            throws LinkApplicationException
    {
        RequestCommons.deleteExpiredRequests(requestRepository);

        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        if (request.getAgentId() == null || !request.getAgentId().equals(user.getId()))
        {
            throw new UserNotAuthorizedException();
        }

        if (!request.getStatus().equals(RequestStatus.LOCKED.toString()))
        {
            throw new RequestStatusException("Request have to be locked to download its files");
        }

        FileObject fileObject = null;
        for (RequestFile requestFile : request.getFiles())
        {
            if (requestFile.getId().equals(fileId))
            {
                fileObject = RequestCommons.getFileObjectFrom(requestFile);
                break;
            }
        }

        if (fileObject == null)
        {
            throw new RequestFileNotFoundException("File with id " + fileId + " not found");
        }

        return fileObject;
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
