package eu.seal.linking.dao;

import eu.seal.linking.model.db.RequestFile;

import org.springframework.data.repository.CrudRepository;

public interface RequestFileRepository extends CrudRepository<RequestFile, Long>
{
}
