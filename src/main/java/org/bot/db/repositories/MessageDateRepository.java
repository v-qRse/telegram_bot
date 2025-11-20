package org.bot.db.repositories;

import org.bot.db.data.MessageDataEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDateRepository extends CrudRepository<MessageDataEntity, String> {
}