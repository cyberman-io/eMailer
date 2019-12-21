package com.cyberman.emailer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class Authentication {

    public static final String AUTH_LOGIN = "login";
    public static final String AUTH_PLAIN = "plain";

    protected abstract AuthCommand getAuthenticationCommand();

    final void setAuthenticationMechanism(AuthCommand command,
                                          SmtpWriter writer,
                                          SmtpReader reader, SmtpListener listener) throws IOException{
        String command_0 = "auth "+command.getAuthenticationType() + SmtpSession.COMMAND_END;
        writer.write(command_0);
        listener.onAuthenticationMechanismResponse(reader.getSmtpResponse());
    }

    protected abstract void authenticate(AuthCommand command,
                                         SmtpWriter writer,
                                         SmtpReader reader, SmtpListener listener) throws IOException;

    public static class AuthCommand{
        private String authenticationType;
        private List<String> authenticationValues;

        public AuthCommand(String authenticationType) {
            this.authenticationType = authenticationType;
            authenticationValues = new ArrayList<>();
        }

        public boolean addValue(String value){
            return authenticationValues.add(value);
        }

        public String getAuthenticationType() {
            return authenticationType;
        }

        public String getValue(int index){
            return authenticationValues.get(index);
        }
    }
}
