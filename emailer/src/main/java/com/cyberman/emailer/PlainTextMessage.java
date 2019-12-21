package com.cyberman.emailer;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public  class PlainTextMessage extends Message {

    private String subject,text;
    private String senderName = "";

    public PlainTextMessage(String from, String recipient) {
        super(from, recipient);
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSenderName(String senderName){
        this.senderName = senderName;
    }

    @Override
    protected void sendMessage(SmtpWriter writer, SmtpReader reader, SmtpListener listener) throws IOException {
        writer.write( "Date: "+getDate() + "\n"+
                "From: " + senderName +"<" +getFrom()+ ">\n"+
                "Subject: " + subject + "\n"+
                "To: " + getRecipient() + "\n\n");

        writer.write( text+"\n");

        writer.write(".\r\n");

        listener.onDataResponse(reader.getSmtpResponse());
    }

    String getSubject() {
        return subject;
    }

    String getSenderName() {
        return senderName;
    }

     String getText() {
        return text;
    }

     String getDate(){
        return DateFormat.getDateInstance(DateFormat.FULL, Locale.US)
                .format(new Date());
    }
}
