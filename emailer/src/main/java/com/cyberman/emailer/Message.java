package com.cyberman.emailer;

import java.io.IOException;

abstract class Message {

    private String from;
    private String recipient;

    public Message(String from, String recipient) {
        this.from = from;
        this.recipient = recipient;
    }


    String getFrom() {
        return from;
    }

    String getRecipient() {
        return recipient;
    }

    protected abstract void sendMessage(SmtpWriter writer, SmtpReader reader, SmtpListener listener) throws IOException;
}
