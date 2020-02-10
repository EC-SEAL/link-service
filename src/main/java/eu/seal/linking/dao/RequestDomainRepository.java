package eu.seal.linking.dao;

import eu.seal.linking.model.db.RequestDomain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface RequestDomainRepository extends CrudRepository<RequestDomain, Long>
{
    public List<RequestDomain> findByDomainIn(java.util.List<String> domains);
}
