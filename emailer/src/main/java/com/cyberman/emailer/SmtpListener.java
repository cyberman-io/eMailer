package com.cyberman.emailer;

import java.io.IOException;

interface SmtpListener {

      void onConnect(SmtpResponse response);

      void onHeloResponse(SmtpResponse response);

      void onStartTLSResponse(SmtpResponse response);

      void onTLSStarted();

      void onAuthenticationMechanismResponse(SmtpResponse response);

      void onAuthenticateResponse(SmtpResponse response);

      void onMailFromResponse(SmtpResponse response);

      void onRecipientToResponse(SmtpResponse response);

      void onDataRequestPermission(SmtpResponse response);

      void onDataResponse(SmtpResponse response);

      void onCloseResponse(SmtpResponse response);

      void onSuccess();

      void onServerError(SmtpResponse response);

      void onError(IOException e);
}
