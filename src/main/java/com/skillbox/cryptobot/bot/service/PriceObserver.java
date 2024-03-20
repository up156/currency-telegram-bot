package com.skillbox.cryptobot.bot.service;
import com.skillbox.cryptobot.client.BinanceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class PriceObserver {

    private final SubscriberService subscriberService;

    private final BinanceClient binanceClient;

    public PriceObserver(SubscriberService subscriberService, BinanceClient binanceClient) {
        this.subscriberService = subscriberService;
        this.binanceClient = binanceClient;
    }

    @Scheduled(fixedDelayString = "${app.fixedDelay.in.milliseconds}")
    public void checkPrice() {
        try {
            Double currentPrice = binanceClient.getBitcoinPrice();
            log.info("PriceObserver checked for currentPrice: {}", currentPrice);
                subscriberService.workWithNewPrice(currentPrice);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
