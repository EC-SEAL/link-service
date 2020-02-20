package eu.seal.linking.services;

import eu.seal.linking.dao.RequestDomainRepository;
import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.exceptions.RequestStatusException;
import eu.seal.linking.exceptions.UserNotAuthorizedException;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.User;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestDomain;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.services.commons.RequestCommons;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValidatorService
{
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestDomainRepository requestDomainRepository;

    public List<LinkRequest> getRequestsByDomain(List<String> domains) throws RequestException
    {
        List<RequestDomain> requestDomains = requestDomainRepository.findByDomainIn(domains);

        List<Request> requests = requestRepository.findByDomainsIn(requestDomains);

        List<LinkRequest> linkRequests = new ArrayList<LinkRequest>();
        for (Request request : requests)
        {
            linkRequests.add(RequestCommons.getLinkRequestFrom(request));
        }

        return linkRequests;
    }

    @Transactional
    public void lockRequest(String requestUid, User user) throws LinkApplicationException
    {
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
        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);

        if (request.getAgentId() == null || !request.getAgentId().equals(user.getId()))
        {
            throw new UserNotAuthorizedException();
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
        Request request = RequestCommons.getRequestFrom(requestUid, requestRepository);
        checkUserPermissionDomains(request.getDomains(), user.getEntitlements());

        return RequestCommons.getLinkRequestFrom(request);
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