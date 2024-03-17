package com.skillbox.cryptobot.bot.service;

import com.skillbox.cryptobot.bot.entity.Subscriber;
import com.skillbox.cryptobot.bot.repository.SubscriberRepository;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public void saveSubscriber(Subscriber subscriber) {
        if (!checkIfExists(subscriber)) {
            subscriberRepository.save(subscriber);
        }
    }

    private boolean checkIfExists(Subscriber subscriber) {
        return subscriberRepository.existsByTgId(subscriber.getTgId());
    }
}
