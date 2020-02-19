package eu.seal.linking.services;

import eu.seal.linking.dao.RequestDomainRepository;
import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestDomain;
import eu.seal.linking.services.commons.RequestCommons;

import java.util.ArrayList;
import java.util.List;

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
}
