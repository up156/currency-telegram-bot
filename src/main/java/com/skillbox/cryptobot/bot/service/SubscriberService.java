package com.skillbox.cryptobot.bot.service;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.bot.entity.Subscriber;
import com.skillbox.cryptobot.bot.repository.SubscriberRepository;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    @Value("${app.intervalForNotifications.in.minutes}")
    private Integer intervalForNotification;

    private final CryptoBot cryptoBot;

    public SubscriberService(SubscriberRepository subscriberRepository, @Lazy CryptoBot cryptoBot) {
        this.subscriberRepository = subscriberRepository;
        this.cryptoBot = cryptoBot;
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

    public boolean checkIfExists(Long id) {
        return subscriberRepository.existsByTgId(id);
    }

    public Subscriber getSubscriber(Long id) {

        Subscriber subscriber = subscriberRepository.findSubscriberByTgId(id);
        subscriber = subscriber == null ? saveSubscriber(id) : subscriber;
        return subscriber;
    }

    public void workWithNewPrice(Double currentPrice) {

        log.info("SubscriberService work with price: " + currentPrice);
        List<Subscriber> listForNotify = subscriberRepository.findSubscribersByPriceGreaterThan(currentPrice);
        for (Subscriber subscriber : listForNotify) {
            if (subscriber.getLastNotified() == null || subscriber.getLastNotified().isBefore(Instant.now()
                    .minus(intervalForNotification, ChronoUnit.MINUTES))) {
                cryptoBot.sendMessage(subscriber.getTgId(), "Пора покупать, стоимость биткоина: " +
                        TextUtil.toString(currentPrice) + " USD");
                subscriber.setLastNotified(Instant.now());
                updateSubscriber(subscriber);
            }
        }
    }
}