package com.skillbox.cryptobot.bot.repository;

import com.skillbox.cryptobot.bot.entity.Subscriber;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SubscriberRepository extends CrudRepository<Subscriber, UUID> {

    Boolean existsByTgId(Long tgId);

    Subscriber findSubscriberByTgId(Long tgId);
}
