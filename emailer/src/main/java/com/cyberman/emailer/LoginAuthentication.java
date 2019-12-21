package com.cyberman.emailer;

import android.util.Base64;

import java.io.IOException;

public final class LoginAuthentication extends Authentication {

    private String user,password;

    public LoginAuthentication(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    protected AuthCommand getAuthenticationCommand() {
        AuthCommand command = new AuthCommand(Authentication.AUTH_LOGIN);
        command.addValue(user);
        command.addValue(password);
        return command;
    }


    @Override
    protected void authenticate(AuthCommand command,
                                SmtpWriter writer,
                                SmtpReader reader,
                                SmtpListener listener) throws IOException {

        String userName = Base64.encodeToString(command.getValue(0).getBytes(),Base64.DEFAULT);

        String userPassword = Base64.encodeToString(command.getValue(1).getBytes(),Base64.DEFAULT);

        writer.write(userName);

        SmtpResponse response = reader.getSmtpResponse();

        if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Authentication_Mechanism_Accepted){
            writer.write(userPassword);
            listener.onAuthenticateResponse(reader.getSmtpResponse());
        }
        else {
            listener.onServerError(response);
        }
    }
}
