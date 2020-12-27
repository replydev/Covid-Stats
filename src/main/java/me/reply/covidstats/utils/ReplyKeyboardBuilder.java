package me.reply.covidstats.utils;

import org.telegram.telegrambots.meta.api.objects.LoginUrl;
import org.telegram.telegrambots.meta.api.objects.games.CallbackGame;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class ReplyKeyboardBuilder {

    private ReplyKeyboardBuilder() {
    }

    public static ReplyKeyboardMarkupBuilder createReply() {
        return new ReplyKeyboardMarkupBuilder();
    }

    public static InlineKeyboardMarkupBuilder createInline() {
        return new InlineKeyboardMarkupBuilder();
    }

    @SuppressWarnings("unused")
    public static class ReplyKeyboardMarkupBuilder {

        private final List<KeyboardRow> keyboard = new ArrayList<>();
        private KeyboardRow row = null;

        ReplyKeyboardMarkupBuilder() {
        }

        public ReplyKeyboardMarkupBuilder row() {
            if (row != null) {
                keyboard.add(row);
            }
            row = new KeyboardRow();
            return this;
        }

        public ReplyKeyboardMarkupBuilder addText(String text) {
            row.add(text);
            return this;
        }

        public ReplyKeyboardMarkupBuilder addRequestContact(String text) {
            row.add(
                    KeyboardButton.builder()
                            .text(text)
                            .requestContact(true)
                    .build()
            );
            return this;
        }

        public ReplyKeyboardMarkupBuilder addRequestLocation(String text) {
            row.add(
                    KeyboardButton.builder()
                    .text(text)
                    .requestLocation(true)
                    .build()
            );
            return this;
        }

        public ReplyKeyboardMarkup build() {
            if (row != null) {
                keyboard.add(row);
            }
            return ReplyKeyboardMarkup.builder()
                    .keyboard(keyboard)
                    .resizeKeyboard(true)
                    .build();
        }
    }

    @SuppressWarnings("unused")
    public static class InlineKeyboardMarkupBuilder {

        private final List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        private List<InlineKeyboardButton> row;

        InlineKeyboardMarkupBuilder() {
        }

        public InlineKeyboardMarkupBuilder row() {
            if (row != null) {
                keyboard.add(row);
            }
            row = new LinkedList<>();
            return this;
        }

        public InlineKeyboardMarkupBuilder addUrl(String text, String url) {
            row.add(InlineKeyboardButton.builder().text(text).url(url).build());
            return this;
        }

        public InlineKeyboardMarkupBuilder addLoginUrl(String text, LoginUrl loginUrl) {
            row.add(InlineKeyboardButton.builder().text(text).loginUrl(loginUrl).build());
            return this;
        }

        public InlineKeyboardMarkupBuilder addCallbackData(String text, String callbackData) {
            row.add(InlineKeyboardButton.builder().text(text).callbackData(callbackData).build());
            return this;
        }

        public InlineKeyboardMarkupBuilder addSwitchInlineQuery(String text, String switchInlineQuery) {
            row.add(InlineKeyboardButton.builder().text(text).switchInlineQuery(switchInlineQuery).build());
            return this;
        }

        public InlineKeyboardMarkupBuilder addsetSwitchInlineQueryCurrentChat(String text, String switchInlineQueryCurrentChat) {
            row.add(InlineKeyboardButton.builder().text(text).switchInlineQueryCurrentChat(switchInlineQueryCurrentChat).build());
            return this;
        }

        public InlineKeyboardMarkupBuilder addCallbackGame(String text, CallbackGame callbackGame) {
            row.add(InlineKeyboardButton.builder().text(text).callbackGame(callbackGame).build());
            return this;
        }

        public InlineKeyboardMarkupBuilder addPay(String text, boolean pay) {
            row.add(InlineKeyboardButton.builder().text(text).pay(pay).build());
            return this;
        }

        public InlineKeyboardMarkup build() {
            return InlineKeyboardMarkup.builder()
                    .keyboard(keyboard)
                    .build();
        }
    }
}