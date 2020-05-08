package eu.seal.linking.services;

import eu.seal.linking.dao.RequestDomainRepository;
import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.exceptions.RequestFileNotFoundException;
import eu.seal.linking.exceptions.RequestStatusException;
import eu.seal.linking.exceptions.UserNotAuthorizedException;
import eu.seal.linking.model.FileObject;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.User;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestDomain;
import eu.seal.linking.model.db.RequestFile;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.services.commons.RequestCommons;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ValidatorService
{
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestDomainRepository requestDomainRepository;

    private final static Logger LOG = LoggerFactory.getLogger(ValidatorService.class);

    private static String REQUEST_LLOA;

    private static int REQUEST_VALIDITY_DAYS;

    @Value("${linking.request.lloa}")
    public void setRequestLloa(String requestLloa) {
        REQUEST_LLOA = requestLloa;
    }

    @Value("${linking.request.validityDays}")
    public void setRequestValidityDays(int requestValidityDays)
    {
        REQUEST_VALIDITY_DAYS = requestValidityDays;
    }

    public List<LinkRequest> getRequestsByDomain(List<String> domains) throws RequestException
    {
        RequestCommons.deleteExpiredRequests(requestRepository);

        List<RequestDomain> requestDomains = requestDomainRepository.findByDomainIn(domains);

        List<Request> requests = requestRepository.findByDomainsIn(requestDomains);

        List<LinkRequest> linkRequests = new ArrayList<LinkRequest>();
        for (Request request : requests)
        {
            linkRequests.add(RequestCommons.getLinkRequestFrom(request, RequestCommons.REQ_ADD_ALL_FIELDS));
        }

        return linkRequests;
    }

    @Transactional
    public void lockRequest(String requestUid, User user) throws LinkApplicationException
    {
        RequestCommons.deleteExpiredRequests(requestRepository);

        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        if (!request.getStatus().equals(RequestStatus.PENDING.toString()))
        {
            throw new RequestStatusException("Request is not pending");
        }

        checkUserPermissionDomains(request.getDomains(), user.getEntitlements());

        request.setAgentId(user.getId());
        request.setStatus(RequestStatus.LOCKED.toString());
        request.setLastUpdate(new Date());

        requestRepository.save(request);
    }

    @Transactional
    public void unlockRequest(String requestUid, User user) throws LinkApplicationException
    {
        RequestCommons.deleteExpiredRequests(requestRepository);

        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        if (request.getAgentId() == null || !request.getAgentId().equals(user.getId()))
        {
            throw new UserNotAuthorizedException("You cannot unlock this request");
        }

        if (!request.getStatus().equals(RequestStatus.LOCKED.toString()))
        {
            throw new RequestStatusException("Request is not locked");
        }

        request.setAgentId(null);
        request.setStatus(RequestStatus.PENDING.toString());
        request.setLastUpdate(new Date());

        requestRepository.save(request);
    }

    public LinkRequest getRequest(String requestUid, User user) throws LinkApplicationException
    {
        RequestCommons.deleteExpiredRequests(requestRepository);

        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);
        checkUserPermissionDomains(request.getDomains(), user.getEntitlements());

        return RequestCommons.getLinkRequestFrom(request, RequestCommons.REQ_ADD_ALL_FIELDS);
    }

    @Transactional
    public void approveRequest(String requestUid, User user) throws LinkApplicationException
    {
        RequestCommons.deleteExpiredRequests(requestRepository);

        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        if (request.getAgentId() == null || !request.getAgentId().equals(user.getId()))
        {
            throw new UserNotAuthorizedException();
        }

        if (!request.getStatus().equals(RequestStatus.LOCKED.toString()))
        {
            throw new RequestStatusException("Request have to be locked before approve it");
        }

        request.setStatus(RequestStatus.ACCEPTED.toString());
        request.setLastUpdate(new Date());

        LinkRequest linkRequest = RequestCommons.getLinkRequestFrom(request, RequestCommons.REQ_NOT_ADD_ALL_FIELDS);
        linkRequest.setLloa(REQUEST_LLOA);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        linkRequest.setIssued(sdf.format(new Date()));

        String expiration = "None";
        if (REQUEST_VALIDITY_DAYS > 0)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, REQUEST_VALIDITY_DAYS);
            Date expirationDate = calendar.getTime();
            expiration = sdf.format(expirationDate);
        }
        linkRequest.setExpiration(expiration);

        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            String strRequest = objectMapper.writeValueAsString(linkRequest);
            request.setStrRequest(strRequest);
        } catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
            throw new LinkInternalException();
        }

        requestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(String requestUid, User user) throws LinkApplicationException
    {
        RequestCommons.deleteExpiredRequests(requestRepository);

        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        if (request.getAgentId() == null || !request.getAgentId().equals(user.getId()))
        {
            throw new UserNotAuthorizedException();
        }

        if (!request.getStatus().equals(RequestStatus.LOCKED.toString()))
        {
            throw new RequestStatusException("Request have to be locked before reject it");
        }

        request.setStatus(RequestStatus.REJECTED.toString());
        request.setLastUpdate(new Date());
    }

    private void checkUserPermissionDomains(List<RequestDomain> requestDomains, List<String> userEntitlements)
            throws UserNotAuthorizedException
    {
        boolean authorized = false;
        for (RequestDomain requestDomain : requestDomains)
        {
            if (userEntitlements.contains(requestDomain.getDomain()))
            {
                authorized = true;
                break;
            }
        }

        if (!authorized)
        {
            throw new UserNotAuthorizedException("Not access to request domains");
        }
    }

}
