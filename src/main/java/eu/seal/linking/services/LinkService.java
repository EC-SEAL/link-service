package eu.seal.linking.services;

import eu.seal.linking.dao.RequestDomainRepository;
import eu.seal.linking.dao.RequestRepository;
import eu.seal.linking.exceptions.LinkApplicationException;
import eu.seal.linking.exceptions.LinkInternalException;
import eu.seal.linking.exceptions.RequestException;
import eu.seal.linking.exceptions.RequestNotFoundException;
import eu.seal.linking.model.LinkRequest;
import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestDomain;
import eu.seal.linking.model.enums.RequestStatus;
import eu.seal.linking.utils.CryptoUtils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LinkService
{
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestDomainRepository requestDomainRepository;

    //public HttpResponse startLinkRequest();

    private final static Logger LOG = LoggerFactory.getLogger(LinkService.class);

    public LinkRequest storeNewRequest(String strRequest) throws LinkApplicationException
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            LinkRequest linkRequest = objectMapper.readValue(strRequest, LinkRequest.class);
            Request request = initializeRequest(linkRequest, strRequest);
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

    public String getRequestStatus(String uid) throws RequestNotFoundException
    {
        List<Request> requests = requestRepository.findByUid(uid);

        if (requests.size() == 0)
        {
            throw new RequestNotFoundException("Request " + uid + " not found");
        }

        return  requests.get(0).getStatus();
    }

    public void cancelRequest(String uid)
    {
        List<Request> requests = requestRepository.findByUid(uid);

        if (requests.size() > 0)
        {
            requestRepository.delete(requests.get(0));
        }
    }


    private static Request initializeRequest(LinkRequest linkRequest, String strRequest) throws LinkInternalException
    {
        Date currentDate = new Date();

        Request request = new Request();
        request.setUid(UUID.randomUUID().toString());
        try
        {
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
}
