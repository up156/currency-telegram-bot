package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.bot.entity.Subscriber;
import com.skillbox.cryptobot.bot.service.SubscriberService;
import com.skillbox.cryptobot.client.BinanceClient;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Arrays;

/**
 * Обработка команды подписки на курс валюты
 */
@Service
@Slf4j
public class SubscribeCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    private final BinanceClient binanceClient;

    public SubscribeCommand(SubscriberService subscriberService, BinanceClient binanceClient) {
        this.subscriberService = subscriberService;
        this.binanceClient = binanceClient;
    }

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        log.info("user with id: {} send /subscribe command with price: {}", message.getFrom().getId(), message.getText());

        Double price = getValue(message);
        if (price == null) {
            sendMessage(absSender, message, "Неверное значение для команды");
            return;
        }
        Subscriber subscriber = subscriberService.getSubscriber(message.getFrom().getId());
        subscriber.setPrice(price);
        subscriberService.updateSubscriber(subscriber);
        sendCurrentBtcPrice(absSender, message);

        sendMessage(absSender, message, "Новая подписка установлена на цену: " + price + " USD");
    }

    private Double getValue(Message message) {
        Double value;
        String input = Arrays.stream(message.getText().split(" "))
                .filter(s -> s.matches("[0-9]+"))
                .findFirst().orElse("");
        if (input.equals("")) {
            return null;
        }
        value = Double.valueOf(input);
        return value;
    }

    private void sendCurrentBtcPrice(AbsSender absSender, Message message) {
        try {
            sendMessage(absSender, message, "Текущая цена биткойна: " + TextUtil.toString(binanceClient.getBitcoinPrice()));
        } catch (IOException e) {
            log.info("Error occurred in /subscribe command");
        }
    }

    private void sendMessage(AbsSender absSender, Message message, String reply) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        try {
            answer.setText(reply);
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /subscribe command", e);
        }
    }
}