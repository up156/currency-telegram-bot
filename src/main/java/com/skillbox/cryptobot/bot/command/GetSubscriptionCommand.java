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

@Service
@Slf4j
@AllArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    private final SubscriberService subscriberService;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        Long userId = message.getFrom().getId();
        log.info("user with id: {} send /get_subscription command", userId);
        if (!subscriberService.checkIfExists(userId)) {
            sendNoSubscriptionMessage(absSender, message);
            return;
        }

        Subscriber subscriber = subscriberService.getSubscriber(message.getFrom().getId());
        Double currentPrice = subscriber.getPrice();
        if (currentPrice == null) {
            sendNoSubscriptionMessage(absSender, message);
            return;
        }
        sendMessage(absSender, message, "Вы подписаны на стоимость биткоина " + currentPrice + " USD");
    }

    private void sendNoSubscriptionMessage(AbsSender absSender, Message message) {
        sendMessage(absSender, message, "Активные подписки отсутствуют");
    }

    private void sendMessage(AbsSender absSender, Message message, String reply) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        try {
            answer.setText(reply);
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /get_subscription command", e);
        }
    }
}