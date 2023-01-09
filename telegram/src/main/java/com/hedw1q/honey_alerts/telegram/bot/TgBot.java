package com.hedw1q.honey_alerts.telegram.bot;


import com.hedw1q.honey_alerts.telegram.model.ChatHistoryInfo;
import com.hedw1q.honey_alerts.telegram.service.ChatHistoryInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Map;

/**
 * @author hedw1q
 */
@Component
@Slf4j
public class TgBot extends TelegramLongPollingCommandBot {
    public static final String PARAMETERS_SEPARATOR = " ";

    private static final Map<String, String> getenv = System.getenv();
    private static final String PARSE_MODE = "HTML";

    private final String BOT_NAME = getenv.get("telegram.botname");
    private final String BOT_TOKEN = getenv.get("telegram.bottoken");

    @Autowired
    private ChatHistoryInfoService chatHistoryInfoService;

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        TgEventStrategy strategy;

        if (update.hasCallbackQuery()) {
            strategy = new TgCallbackEventStrategy();
        } else strategy = new TgMessageEventStrategy();

        ChatHistoryInfo chatInfo = chatHistoryInfoService.findByChatId(strategy.getChat(update).getId()).orElseGet(() -> new ChatHistoryInfo(strategy.getChat(update).getId()));

        String query = strategy.getQuery(update);
        log.debug("Query: {}", query);
        String[] parameters = strategy.parseQuery(query);
        String command = query.contains(PARAMETERS_SEPARATOR) ? query.substring(1, query.indexOf(PARAMETERS_SEPARATOR)) : query.substring(1);

        if (getRegisteredCommands().stream()
                .map(IBotCommand::getCommandIdentifier)
                .toList()
                .contains(command)) {
            getRegisteredCommand(command).processMessage(this,strategy.getMessage(update),parameters);
        }else if(chatInfo.getChatState().equals(ChatHistoryInfo.ChatState.ADD)){
            getRegisteredCommand("subscribe").processMessage(this, strategy.getMessage(update), parameters);
        }

//        if (commandAggregator.getCommands().containsKey(command)) {
//            if (commandAggregator.getCommands().get(command).getClass().equals(CancelCommand.class))
//                parameters = new String[]{update.getCallbackQuery().getMessage().getMessageId().toString()};
//            commandAggregator.getCommands().get(command).execute(this, strategy.getUser(update), strategy.getChat(update), parameters);
//        } else if (chatInfo.getChatState() == ChatHistoryInfo.ChatState.ADD) { //after choosing streamer
//            commandAggregator.getCommands().get("/subscribe").execute(this, strategy.getUser(update), strategy.getChat(update), parameters);
//        }
    }

    /**
     * Send simple text message to channel
     *
     * @param chatId long chat id
     * @param text   String  message text
     * @return Message object send. Null if exception thrown
     */
    public Message sendTextMessageToChannel(Long chatId, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        answer.setParseMode(PARSE_MODE);
        try {
            return execute(answer);
        } catch (TelegramApiException e) {
            log.error("Telegram send text message error", e);
            return null;
        }
    }

    /**
     * Send simple text message to channel with chat keyboard
     *
     * @param chatId   long     chat id
     * @param text     String      message text
     * @param keyboard - ReplyKeyboard
     * @return Message object send. Null if exception thrown
     */
    public Message sendTextMessageToChannel(Long chatId, String text, ReplyKeyboard keyboard) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        answer.setParseMode(PARSE_MODE);
        answer.setReplyMarkup(keyboard);
        try {
            return execute(answer);
        } catch (TelegramApiException e) {
            log.error("Telegram send text message error", e);
            return null;
        }
    }

    /**
     * Send simple text message to channel with disable preview parameter
     *
     * @param chatId         long     chat id
     * @param text           String      message text
     * @param disablePreview - boolean disable link preview
     * @return Message object send. Null if exception thrown
     */
    public Message sendTextMessageToChannel(Long chatId, String text, boolean disablePreview) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        answer.setParseMode(PARSE_MODE);
        answer.setDisableWebPagePreview(disablePreview);
        try {
            return execute(answer);
        } catch (TelegramApiException e) {
            log.error("Telegram send text message error", e);
            return null;
        }
    }

    /**
     * Send message edit
     *
     * @param chatId    long chat id
     * @param messageId Integer message id to edit
     * @param text      String new Text
     */
    public void sendEditMessage(Long chatId, Integer messageId, String text) {
        EditMessageText answer = new EditMessageText();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        answer.setMessageId(messageId);
        answer.setParseMode(PARSE_MODE);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.error("Telegram send edit text message error", e);
        }
    }

    /**
     * Send message edit with InlineKeyboard markup
     *
     * @param chatId         long chat id
     * @param messageId      Integer message id to edit
     * @param text           String new Text
     * @param keyboardMarkup InlineKeyboardMarkup
     */
    public void sendEditMessage(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageText answer = new EditMessageText();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        answer.setMessageId(messageId);
        answer.setReplyMarkup(keyboardMarkup);
        answer.setParseMode(PARSE_MODE);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.error("Telegram send edit text message error", e);
        }
    }

    /**
     * Send message edit only InlineKeyboard markup
     *
     * @param chatId         long chat id
     * @param messageId      Integer message id to edit
     * @param keyboardMarkup InlineKeyboardMarkup
     */
    public void sendEditKeyboardMessage(Long chatId, Integer messageId, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageReplyMarkup answer = new EditMessageReplyMarkup();
        answer.setChatId(chatId.toString());
        answer.setMessageId(messageId);
        answer.setReplyMarkup(keyboardMarkup);
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.error("Telegram send edit text message error", e);
        }
    }


    /**
     * Send empty answer to pressed button. Removes loading icon on button
     *
     * @param callbackId - button callback id
     */
    public void sendEmptyAnswerCallback(String callbackId) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackId);
        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            log.error("Send answer callback error", e);
        }
    }

    /**
     * Send message with text and attachment, such as a video or GIF or image. Attachment retrieved by URL
     *
     * @param chatId long chat id
     * @param url    String url to retrieve attachment
     * @param text   String message text
     * @return Message object send. Null if exception thrown
     */
    public Message sendAttachmentMessageToChannel(Long chatId, String url, String text) {
        try {
            URL urlObject = new URL(url);
            URLConnection conn = urlObject.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
            conn.connect();
            String contentType = conn.getContentType().trim().split(";")[0];
//           final  Pattern pattern = Pattern.compile("<@.+>");
//            final Matcher matcher = pattern.matcher(contentType);
            //     File file = new File(urlObject.getFile());
            // FileUtils.copyInputStreamToFile(conn.getInputStream(), file);

            return switch (contentType) {
                case "image/png", "image/bmp", "image/tiff", "image/jpeg" ->
                        sendImageMessageToChannel(chatId, conn.getInputStream(), text);
                case "image/gif",
                        "video/mpeg", "video/mp4", "video/ogg",
                        "video/quicktime", "video/webm",
                        "video/x-ms-wmv", "video/x-flv", "video/x-msvideo" ->
                        sendAnimationMessageToChannel(chatId, conn.getInputStream(), text);
                default -> {
                    log.warn("Unsupported Content-Type: {}", contentType);
                    yield null;
                }
            };
        } catch (MalformedURLException urlException) {
            sendTextMessageToChannel(chatId, text);
            log.warn("Malformed URL", urlException);
            return null;
        } catch (IOException ioException) {
            sendTextMessageToChannel(chatId, text);
            log.warn("Stream image retrieve exception", ioException);
            return null;
        } catch (TelegramApiException telegramApiException) {
            log.error("Telegram send text message error", telegramApiException);
            return null;
        }
    }

    /**
     * Hides inline keyboard from chat
     *
     * @param chatId long chat id
     * @return Message object send. Null if exception thrown
     */
    public Message sendHideKeyboard(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());

        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        replyKeyboardRemove.setSelective(true);
        sendMessage.setReplyMarkup(replyKeyboardRemove);

        try {
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Telegram send hide keyboard message error", e);
            return null;
        }
    }

    /**
     * Send message with animated image
     *
     * @param chatId      long chat id
     * @param inputStream InputStream image(gif) stream
     * @param text        String message text
     * @return Message object send. Null if exception thrown
     */
    private Message sendImageMessageToChannel(Long chatId, InputStream inputStream, String text) throws TelegramApiException {
        SendPhoto msg = new SendPhoto();
        msg.setPhoto(new InputFile(inputStream, "image"));
        msg.setChatId(chatId.toString());
        msg.setCaption(text);
        msg.setParseMode(PARSE_MODE);
        return execute(msg);
    }

    /**
     * Send message with video
     *
     * @param chatId      long chat id
     * @param inputStream InputStream video stream
     * @param text        String message text
     * @return Message object send. Null if exception thrown
     */
    private Message sendAnimationMessageToChannel(Long chatId, InputStream inputStream, String text) throws TelegramApiException {
        SendVideo msg = new SendVideo();
        msg.setVideo(new InputFile(inputStream, "video"));
        msg.setChatId(chatId.toString());
        msg.setCaption(text);
        msg.setParseMode(PARSE_MODE);
        return execute(msg);
    }
}

