package com.cyberman.emailerexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cyberman.emailer.LoginAuthentication;
import com.cyberman.emailer.PlainTextMessage;
import com.cyberman.emailer.SmtpSession;

public class ExampleActivity extends AppCompatActivity implements SmtpSession.OnCompleteListener {

    private EditText recipientField,subjectField,messageField;
    private Button sendButton;
    ProgressDialog dialog;

    final private String smtpHost = ""; //smtp Host address ex : smtp.yandex.com;
    final private String user = "";// user name or email;
    final private String userPassword = "";//user password;
    final private String senderName = "";//optional name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recipientField = findViewById(R.id.id_recipient);
        subjectField = findViewById(R.id.id_subject);
        messageField = findViewById(R.id.id_message);
        sendButton = findViewById(R.id.id_send);
        dialog = new ProgressDialog();
        dialog.setCancelable(false);
    }

    public void send(View view) {
        sendButton.setEnabled(false);
        String recipient = recipientField.getText().toString();
        String subject = subjectField.getText().toString();
        String message = messageField.getText().toString();

        SmtpSession session = new SmtpSession(smtpHost,SmtpSession.SMTP_TLS_PORT_DEFAULT);
        session.useAuthentication(new LoginAuthentication(user, userPassword));
        session.setOnCompleteListener(this);
        session.enableStartTLS();

        PlainTextMessage m = new PlainTextMessage(user,recipient);
        m.setSenderName(senderName);
        m.setSubject(subject);
        m.setText(message);

        session.setMessage(m);
        session.send();
        dialog.showNow(getSupportFragmentManager(),null);
    }

    @Override
    public void onSendSuccess() {
        dialog.dismiss();
        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
        sendButton.setEnabled(true);
    }

    @Override
    public void onServerError(int responseCode, String responseMessage) {
        dialog.dismiss();
        Toast.makeText(this, "onServerError : "+responseMessage, Toast.LENGTH_SHORT).show();
        sendButton.setEnabled(true);
    }

    @Override
    public void onError(Exception e) {
        dialog.dismiss();
        Toast.makeText(this, "onError : "+e.getMessage(), Toast.LENGTH_SHORT).show();
        sendButton.setEnabled(true);
    }
}
