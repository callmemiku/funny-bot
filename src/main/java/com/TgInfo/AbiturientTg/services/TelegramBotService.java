package com.TgInfo.AbiturientTg.services;

import com.TgInfo.AbiturientTg.config.BotConfig;
import com.TgInfo.AbiturientTg.model.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

import static com.TgInfo.AbiturientTg.model.InputData.*;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBotService extends TelegramLongPollingBot implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(this);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    final BotConfig botConfig;

    static final String RATING_BUTTON = "RATING_BUTTON";
    static final String DORMITORY_BUTTON = "DORMITORY_BUTTON";
    static final String PROF_BUTTON = "PROF_BUTTON";
    static final String APPLY_BUTTON = "APPLY_BUTTON";
    static final String OCHN_BUTTON = "OCHN_BUTTON";
    static final String ZAOCHN_BUTTON = "ZAOCHN_BUTTON";
    static final String PRIEM_BUTTON = "MORE_BUTTON";
    static final String BACK_BUTTON = "BACK_BUTTON";
    static final String ADRES_BUTTON = "ADRES_BUTTON";
    static final String DOCUMENT_BUTTON = "DOCUMENT_BUTTON";
    static final String ADRES_PRIEM_BUTTON = "ADRES_PRIEM_BUTTON";
    static final String PHOTO_BUTTON = "PHOTO_BUTTON";
    static final String BACK_BUTTON_2 = "BACK_BUTTON_2";

    public TelegramBotService(BotConfig botConfig) {
        this.botConfig = botConfig;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    InputData inputData = MENU;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() && inputData == MENU) {

            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    sendPhotoNoKeyboard(
                            chatId,
                            new InputFile(
                                    Images.SGGK.file(),
                                    "SGGK"
                            )
                    );
                    welcomeCommandSet(chatId);
                    break;
                default:
                    welcomeCommandSet(chatId);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case RATING_BUTTON: {
                    educationCommandSet(chatId);
                    break;
                }
                case OCHN_BUTTON: {
                    Ochnoe(chatId);
                    executeEditMessageText("Очная форма обучения", chatId, messageId);
                    break;
                }
                case ZAOCHN_BUTTON: {
                    Zaochnoe(chatId);
                    executeEditMessageText("Заочная форма обучения", chatId, messageId);
                    break;
                }
                case ADRES_BUTTON: {
                    sendMessageNoKeyboard(chatId, Text.adres.getValue());
                    SendLocation sendLocation = new SendLocation();
                    sendLocation.setChatId(chatId);
                    sendLocation.setLatitude(55.04661926138816);
                    sendLocation.setLongitude(58.97873277885539);
                    try {
                        execute(sendLocation);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                    break;
                }
                case PHOTO_BUTTON: {
                    sendPhotoNoKeyboard(chatId, new InputFile(Images.PHOTO_1.file(), "photo1"));
                    sendPhotoNoKeyboard(chatId, new InputFile(Images.PHOTO_2.file(), "photo2"));
                    sendPhotoNoKeyboard(chatId, new InputFile(Images.PHOTO_3.file(), "photo3"));
                    sendPhotoNoKeyboard(chatId, new InputFile(Images.PHOTO_4.file(), "photo4"));
                    photo(chatId);
                    break;
                }
                case PROF_BUTTON: {
                    profesii(chatId);
                    break;
                }
                case PRIEM_BUTTON: {
                    Priem(chatId);
                    break;
                }
                case DOCUMENT_BUTTON: {
                    neobhodimieDoc(chatId);
                    break;
                }
                case BACK_BUTTON: {
                    welcomeCommandSet(chatId);
                    break;
                }
                case DORMITORY_BUTTON:
                case BACK_BUTTON_2: {
                    dormitory(chatId);
                    break;
                }
                case ADRES_PRIEM_BUTTON: {
                    adresPriem(chatId);
                    break;
                }
            }
        }
    }

    public void welcomeCommandSet(long chatId) { //главное
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Text.start.getValue());
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> firstInLine = new ArrayList<>();
        var ratingButton = new InlineKeyboardButton();
        ratingButton.setText("Специальности");
        ratingButton.setCallbackData(RATING_BUTTON);
        var dormitoryButton = new InlineKeyboardButton();
        dormitoryButton.setText("Общежитие");
        dormitoryButton.setCallbackData(DORMITORY_BUTTON);
        firstInLine.add(ratingButton);
        firstInLine.add(dormitoryButton);
        rowsInLine.add(firstInLine);
        List<InlineKeyboardButton> secondInLine = new ArrayList<>();
        var applyButton = new InlineKeyboardButton();
        applyButton.setText("Подать заявление");
        applyButton.setUrl("https://pro.sgkk.ru/blocks/manage_groups/admission_new/self_application.php?");
        applyButton.setCallbackData(APPLY_BUTTON);
        var ProfButton = new InlineKeyboardButton();
        ProfButton.setText("Профессии");
        ProfButton.setCallbackData(PROF_BUTTON);
        secondInLine.add(applyButton);
        secondInLine.add(ProfButton);
        rowsInLine.add(secondInLine);
        List<InlineKeyboardButton> treeInLine = new ArrayList<>();
        var moreButton = new InlineKeyboardButton();
        moreButton.setText("Приемная комиссия");
        moreButton.setCallbackData(PRIEM_BUTTON);
        treeInLine.add(moreButton);
        rowsInLine.add(treeInLine);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        inputData = MENU;
        executeMessage(message);
    }

    public void educationCommandSet(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Какую форму обучения вы хотите получить?");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> firstInLine = new ArrayList<>();
        var ochnButton = new InlineKeyboardButton();
        ochnButton.setText("Очная форма");
        ochnButton.setCallbackData(OCHN_BUTTON);
        var zaochButton = new InlineKeyboardButton();
        zaochButton.setText("Заочная форма");
        zaochButton.setCallbackData(ZAOCHN_BUTTON);
        firstInLine(message, markupInLine, rowsInLine, firstInLine, ochnButton, zaochButton);
    }

    public void profesii(long chatId) { //профессии
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Text.profesii.getValue());
        markup(message);
    }

    public void photo(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Вернуться назад?");
        anotherMarkup(message, BACK_BUTTON_2);
    }

    public void Ochnoe(long chatId) { //очное
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Text.Ochn.getValue());
        markup(message);
    }

    public void Zaochnoe(long chatId) { //заочное
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Text.Zaochn.getValue());
        markup(message);
    }

    private void markup(SendMessage message) {
        anotherMarkup(message, BACK_BUTTON);
    }

    private void anotherMarkup(SendMessage message, String backButton2) {
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> second = new ArrayList<>();
        var backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData(backButton2);
        second.add(backButton);
        rowsInLine.add(second);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        executeMessage(message);
    }

    public void neobhodimieDoc(long chatId) { //ДОКУМЕНТЫ
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Text.required_documents_for_admission.getValue());
        markup(message);
    }

    public void adresPriem(long chatId) { //адрес приемной комисии
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Text.selection_committee.getValue());
        markup(message);
    }

    public void dormitory(long chatId) { //общежитие
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(Text.mainObshezhitie.getValue());
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> firstInLine = new ArrayList<>();
        var adresButton = new InlineKeyboardButton();
        adresButton.setText("Адрес");
        adresButton.setCallbackData(ADRES_BUTTON);
        var photoDormitory = new InlineKeyboardButton();
        photoDormitory.setText("Фотографии");
        photoDormitory.setCallbackData(PHOTO_BUTTON);
        firstInLine.add(photoDormitory);
        firstInLine.add(adresButton);
        rowsInLine.add(firstInLine);
        List<InlineKeyboardButton> second = new ArrayList<>();
        var backButton = new InlineKeyboardButton();
        List<InlineKeyboardButton> tree = new ArrayList<>();
        rowsInLine.add(tree);
        backButton.setText("Назад");
        backButton.setCallbackData(BACK_BUTTON);
        second.add(backButton);
        rowsInLine.add(second);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        executeMessage(message);
    }

    public void Priem(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Приём заявлений от поступающих начинается с 20 июня и заканчивается 15 августа (на заочную форму обучения до 15 сентября)");
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> firstInLine = new ArrayList<>();
        var DocButton = new InlineKeyboardButton();
        DocButton.setText("Необходимые документы");
        DocButton.setCallbackData(DOCUMENT_BUTTON);
        var AdresButton = new InlineKeyboardButton();
        AdresButton.setText("Адрес");
        AdresButton.setCallbackData(ADRES_PRIEM_BUTTON);
        firstInLine(message, markupInLine, rowsInLine, firstInLine, DocButton, AdresButton);
    }

    private void firstInLine(
            SendMessage message,
            InlineKeyboardMarkup markupInLine,
            List<List<InlineKeyboardButton>> rowsInLine,
            List<InlineKeyboardButton> firstInLine,
            InlineKeyboardButton docButton,
            InlineKeyboardButton adresButton
    ) {
        firstInLine.add(docButton);
        firstInLine.add(adresButton);
        rowsInLine.add(firstInLine);
        List<InlineKeyboardButton> tree = new ArrayList<>();
        var backButton = new InlineKeyboardButton();
        backButton.setText("Назад");
        backButton.setCallbackData(BACK_BUTTON);
        tree.add(backButton);
        rowsInLine.add(tree);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);
        executeMessage(message);
    }

    private void executeEditMessageText(String text, long chatId, long messageId) {
        EditMessageText message = new EditMessageText();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        message.setMessageId((int) messageId);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendPhoto(long chatId, InputFile inputFile, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendPhotoNoKeyboard(long chatId, InputFile inputFile) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(inputFile);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendDocument(long chatId, InputFile inputFile) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(inputFile);
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(long chatId, String text, ReplyKeyboardMarkup replyKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

        executeMessage(sendMessage);
    }

    private void sendMessageNoKeyboard(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        executeMessage(sendMessage);
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
