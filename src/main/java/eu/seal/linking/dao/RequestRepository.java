package eu.seal.linking.dao;

import eu.seal.linking.model.db.Request;
import eu.seal.linking.model.db.RequestDomain;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RequestRepository extends CrudRepository<Request, Long>
{
    public List<Request> findByUid(String uid);

    public List<Request> findDistinctByDomainsIn(List<RequestDomain> requestDomains);

    public List<Request> findDistinctByUidAndDomainsIn(String uid, List<RequestDomain> requestDomains);

    public List<Request> getAllByLastUpdateBefore(Date expirationDate);
}
