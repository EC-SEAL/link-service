package eu.seal.linking.dao;

import eu.seal.linking.model.db.RequestMessage;

import org.springframework.data.repository.CrudRepository;

public interface RequestMessageRepository extends CrudRepository<RequestMessage, Long>
{
}
