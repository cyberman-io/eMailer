package com.cyberman.emailer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SmtpReader extends BufferedReader {

    SmtpReader(Socket socket) throws IOException {
        super(new InputStreamReader(socket.getInputStream()));
    }

    private String readResponse() throws IOException {
        final char[] chars = new char[256];
        StringBuilder builder = new StringBuilder();
        int count ;
        while ((count = read(chars)) > 0 ){
            builder.append(chars,0,count);
            if (count < chars.length) break;
        }
        return builder.toString();
    }

     SmtpResponse getSmtpResponse() throws IOException {
        int responseCode = -1;
        String responseMessage = "";

        Pattern pattern = Pattern.compile("(\\d{3})(.*)\n*");
        Matcher matcher = pattern.matcher(readResponse());
        int code;
        while (matcher.find()){
            code = Integer.parseInt(matcher.group(1));
            if (responseCode != code) responseCode = code;
            responseMessage = responseMessage.concat(matcher.group(2));
        }
        return new SmtpResponse(responseCode,responseMessage);
    }
}
