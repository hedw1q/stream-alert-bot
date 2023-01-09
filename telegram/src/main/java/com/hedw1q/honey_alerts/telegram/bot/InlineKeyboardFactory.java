package com.hedw1q.honey_alerts.telegram.bot;

import com.hedw1q.honey_alerts.share.model.StreamPlatform;
import com.hedw1q.honey_alerts.share.model.SubscriptionDTO;
import com.hedw1q.honey_alerts.telegram.command.AbstractBotCommand;
import com.hedw1q.honey_alerts.telegram.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.ListUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.hedw1q.honey_alerts.telegram.bot.TgBot.PARAMETERS_SEPARATOR;


public class InlineKeyboardFactory {
    public static final int DEFAULT_BUTTONS_PER_ROW = 2;

    public static InlineKeyboardMarkup getMainKeyboard() {
        return getDefaultKeyboard(Arrays.asList(
                createButton("Подписаться", "/subscribe"),
                createButton("Отписаться", "/unsubscribe"),
                createButton("Список подписок", "/list"),
                createButton("Настройки", "/settings")));
    }

    public static InlineKeyboardMarkup getSettingsKeyboard(User user){
        return getParametrizedKeyboard(Arrays.asList(
                createParametrizedButton(createButton((user.isPreviewEnabled() ? "Выключить " : "Включить ") + "превью ссылок", "/settings" + PARAMETERS_SEPARATOR + "preview"), 1),
                createParametrizedButton(createButton((user.isGameChangeNotificationsEnabled() ? "Выключить " : "Включить ") + "уведомления о смене игры на стриме", "/settings" + PARAMETERS_SEPARATOR + "gamenotif"), 2),
                createParametrizedButton(createCancelButton(),3)));
    }

    public static InlineKeyboardMarkup getPlatformChooseKeyboard(AbstractBotCommand command, String... params) {
        List<ParametrizedKeyboardButton> buttons = new ArrayList<>();

        List<String> supportedPlatform = Arrays.stream(StreamPlatform.values())
                .filter(Predicate.not(StreamPlatform.UNKNOWN::equals))
                .map(StreamPlatform::getName).toList();

        StringBuilder paramsString = new StringBuilder();
        for (String param : params) {
            paramsString.append(param).append(PARAMETERS_SEPARATOR);
        }
        for (String platform : supportedPlatform) {
            ParametrizedKeyboardButton button = new ParametrizedKeyboardButton(
                    createButton(platform, "/" + command.getCommandIdentifier() + PARAMETERS_SEPARATOR + paramsString + platform), 1);
            buttons.add(button);
        }
        buttons.add(createParametrizedButton(createCancelButton(),2));
        return getParametrizedKeyboard(buttons);
    }


    public static InlineKeyboardMarkup getUnsubscribeKeyboard(List<SubscriptionDTO> subscriptions) {
        List<ParametrizedKeyboardButton> buttons= createUnsubscribeButtons(subscriptions);
        buttons.add(createParametrizedButton(createCancelButton(),subscriptions.size()+1));
        return getParametrizedKeyboard(buttons);
    }

    public static InlineKeyboardMarkup getEmptyKeyboard() {
        return getDefaultKeyboard(Collections.emptyList());
    }


    //private
    private static InlineKeyboardMarkup getDefaultKeyboard(List<InlineKeyboardButton> buttons) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(getDefaultKeyboardSchema(buttons));

        return keyboardMarkup;
    }

    private static InlineKeyboardMarkup getParametrizedKeyboard(List<ParametrizedKeyboardButton> buttons) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(getParametrizedKeyboardSchema(buttons));

        return keyboardMarkup;
    }

    private static List<List<InlineKeyboardButton>> getDefaultKeyboardSchema(List<InlineKeyboardButton> buttons) {
        return ListUtils.partition(buttons, DEFAULT_BUTTONS_PER_ROW);
    }

    private static List<List<InlineKeyboardButton>> getParametrizedKeyboardSchema(List<ParametrizedKeyboardButton> buttons) {
        Map<Integer, List<ParametrizedKeyboardButton>> map =
                buttons.stream()
                        .collect(Collectors.groupingBy(ParametrizedKeyboardButton::getRowNumber));
        Map<Integer, List<InlineKeyboardButton>> mappedMap = new HashMap<>();
        for (Integer key : map.keySet()) {
            mappedMap.put(key, map.get(key)
                    .stream()
                    .map(ParametrizedKeyboardButton::getButton)
                    .toList());
        }
        return new ArrayList<>(mappedMap.values());
    }

    private static InlineKeyboardButton createButton(String text, String command) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(text);
        inlineKeyboardButton.setCallbackData(command);
        return inlineKeyboardButton;
    }

    private static InlineKeyboardButton createCancelButton() {
        return createButton("Отмена", "/cancel");
    }

    private static ParametrizedKeyboardButton createParametrizedButton(InlineKeyboardButton button, int rowNumber) {
        return new ParametrizedKeyboardButton(button, rowNumber);
    }

    private static List<ParametrizedKeyboardButton> createUnsubscribeButtons(List<SubscriptionDTO> subscriptions) {
        List<ParametrizedKeyboardButton> buttons = new ArrayList<>();
        int i=1;
        for (SubscriptionDTO sub : subscriptions) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(sub.channel().name() + " (" + sub.channel().platform() + ")");
            button.setCallbackData("/unsubscribe" + PARAMETERS_SEPARATOR + sub.channel().name() + PARAMETERS_SEPARATOR + sub.channel().platform());
            buttons.add(new ParametrizedKeyboardButton(button,i));
            i++;
        }
        return buttons;
    }

    @AllArgsConstructor
    @Data
    private static class ParametrizedKeyboardButton {
        private InlineKeyboardButton button;
        private int rowNumber;
    }
}
