package com.cyberman.emailer;

final class SmtpResponse {

    interface RESPONSE_CODE {
         int Service_Ready = 220;
         int Action_OK = 250;
         int Authentication_Mechanism_Accepted = 334;
         int Authentication_Successful =235;
         int Mail_Transfer_Permission_Accepted = 354;
    }

    private int responseCode;
    private String responseMessage;

    SmtpResponse(int responseCode, String responseMessage) {
        this.responseCode = responseCode;
        this.responseMessage = responseMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }
}
