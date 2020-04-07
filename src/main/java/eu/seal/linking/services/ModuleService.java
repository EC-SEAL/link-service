package eu.seal.linking.services;

import eu.seal.linking.dao.RequestDomainRepository;
import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.exceptions.RequestNotFoundException;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.User;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestDomain;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.model.module.RequestInfo;
import eu.seal.linking.services.commons.RequestCommons;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModuleService
{
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestDomainRepository requestDomainRepository;

    private final static Logger LOG = LoggerFactory.getLogger(ValidatorService.class);

    public List<RequestInfo> getAgentRequests(User user)
    {
        List<RequestDomain> requestDomains = requestDomainRepository.findByDomainIn(user.getEntitlements());

        List<Request> requests = requestRepository.findByDomainsIn(requestDomains);

        List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
        for (Request request : requests)
        {
            if (request.getStatus().equals(RequestStatus.PENDING.toString()) || (request.getAgentId() != null &&
                    request.getAgentId().equals(user.getId())))
            {
                requestInfoList.add(getRequestInfoFrom(request));
            }
        }

        return requestInfoList;
    }

    public RequestInfo getRequestInfo(String requestId, User user) throws RequestNotFoundException
    {
        List<RequestDomain> requestDomains = requestDomainRepository.findByDomainIn(user.getEntitlements());

        Request request = null;
        try
        {
            request = requestRepository.findByUidAndDomains(requestId, requestDomains).get(0);

        } catch (Exception e)
        {
            e.printStackTrace();
            throw new RequestNotFoundException();
        }

        return getRequestInfoFrom(request);
    }

    private RequestInfo getRequestInfoFrom(Request request)
    {
        RequestInfo requestInfo = new RequestInfo();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        requestInfo.setId(request.getUid());
        requestInfo.setDate(sdf.format(request.getEntryDate()));
        requestInfo.setStatus(request.getStatus());

        return requestInfo;
    }
}
