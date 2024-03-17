package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.bot.entity.Subscriber;
import com.skillbox.cryptobot.bot.service.SubscriberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Обработка команды отмены подписки на курс валюты
 */
@Service
@Slf4j
@AllArgsConstructor
public class UnsubscribeCommand implements IBotCommand {

    private final SubscriberService subscriberService;


    @Override
    public String getCommandIdentifier() {
        return "unsubscribe";
    }

    @Override
    public String getDescription() {
        return "Отменяет подписку пользователя";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        log.info("user with id: {} send /unsubscribe command", message.getFrom().getId());

        Subscriber subscriber = subscriberService.getSubscriber(message.getFrom().getId());
        subscriber.setPrice(null);
        subscriberService.updateSubscriber(subscriber);
        sendMessage(absSender, message, "Подписка отменена");
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