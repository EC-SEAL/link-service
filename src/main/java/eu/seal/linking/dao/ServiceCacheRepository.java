package eu.seal.linking.dao;

import eu.seal.linking.model.db.ServicesCache;

import org.springframework.data.repository.CrudRepository;

public interface ServiceCacheRepository extends CrudRepository<ServicesCache, String>
{
}
