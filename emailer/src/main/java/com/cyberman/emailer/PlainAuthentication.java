package com.cyberman.emailer;

import android.util.Base64;

import java.io.IOException;

public final class PlainAuthentication extends Authentication {

    private String user,password;

    public PlainAuthentication(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    protected AuthCommand getAuthenticationCommand() {
        AuthCommand command = new AuthCommand(Authentication.AUTH_PLAIN);
        command.addValue(user);
        command.addValue(password);
        return command;
    }

    @Override
    protected void authenticate(AuthCommand command,
                                SmtpWriter writer,
                                SmtpReader reader,
                                SmtpListener listener) throws IOException {

        String plain = "\0" + command.getValue(0) + "\0" + command.getValue(1) ;

        String command_ = Base64.encodeToString(plain.getBytes(),Base64.DEFAULT);

        writer.write(command_);

        listener.onAuthenticateResponse(reader.getSmtpResponse());
    }
}
