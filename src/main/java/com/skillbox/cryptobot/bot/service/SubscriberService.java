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

    public Subscriber saveSubscriber(Long id) {
        if (!checkIfExists(id)) {
            Subscriber subscriber = new Subscriber();
            subscriber.setTgId(id);
            return subscriberRepository.save(subscriber);
        }
        return getSubscriber(id);
    }

    public Subscriber updateSubscriber(Subscriber subscriber) {

        return subscriberRepository.save(subscriber);

    }

    private boolean checkIfExists(Long id) {
        return subscriberRepository.existsByTgId(id);
    }

    public Subscriber getSubscriber(Long id) {

        Subscriber subscriber = subscriberRepository.findSubscriberByTgId(id);
        subscriber = subscriber == null ? saveSubscriber(id) : subscriber;
        return subscriber;
    }
}