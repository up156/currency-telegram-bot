package com.skillbox.cryptobot.bot.repository;

import com.skillbox.cryptobot.bot.entity.Subscriber;
import org.springframework.data.repository.CrudRepository;

public interface SubscriberRepository extends CrudRepository<Subscriber, Long> {

    Boolean existsByTgId(Long tgId);
}
