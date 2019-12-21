package com.cyberman.emailer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

final class SmtpWriter extends DataOutputStream {

    SmtpWriter(Socket socket) throws IOException{
        super(socket.getOutputStream());
    }

    final void write(String s) throws IOException{
        writeBytes(s);
    }
}
