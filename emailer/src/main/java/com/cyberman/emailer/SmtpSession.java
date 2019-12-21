package com.cyberman.emailer;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public final class SmtpSession extends Thread implements SmtpListener {

	private Handler mainHandler = new Handler(Looper.getMainLooper());

	private final String SESSION_THREAD_NAME = "smtp_session_thread";

	public static final int SMTP_PORT_DEFAULT = 25;
	public static final int SMTP_TLS_PORT_DEFAULT = 587;
	public static final int  SMTP_SSL_PORT_DEFAULT = 465;

	private static final int TIME_OUT_DEFAULT = 60000;
	public static final int TIME_OUT_INFINITY = 0;

	private static final String COMMAND_HELO = "helo ";
	private static final String COMMAND_START_TLS = "starttls";
	private static final String COMMAND_MAIL_FROM = "mail from: ";
	private static final String COMMAND_RECIPIENT_TO = "rcpt to: ";
	private static final String COMMAND_DATA= "data";
	private static final String COMMAND_QUIT  = "quit";

	static final String COMMAND_END = "\r\n";

	private String smtpHost;
	private int smtpPort;

	private Socket socket;
	private SmtpReader reader;
	private SmtpWriter writer;

	private int timeOut = TIME_OUT_DEFAULT;
	private boolean doEnableSSL = false;
	private boolean doStartTLS = false;
	private boolean isUseAuth = false;

	private Authentication authentication;
	private Message message;
	private OnCompleteListener listener;

	private boolean initialState = true;

	public SmtpSession(String smtpHost){
		this.smtpHost = smtpHost;
		this.smtpPort = SMTP_PORT_DEFAULT;
	}

	public SmtpSession(String smtpHost, int smtpPort) {
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
	}

	public  void setTimeOut(int timeInMilli){
		this.timeOut = timeInMilli;
	}

	public  void enableSSL(){
		doEnableSSL = true;
	}

	public  void enableStartTLS(){
		doStartTLS = true;
	}

	public  void useAuthentication(Authentication authentication){
		isUseAuth = true;
		this.authentication = authentication;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setOnCompleteListener(OnCompleteListener listener) {
		this.listener = listener;
	}


	public void send(){
		setName(SESSION_THREAD_NAME);
		start();
	}

	@Override
	public void run() {
		if (smtpHost == null) {
			onError(new IOException("smtpServerHost must be No Null."));
		}
		else {
			connect();
		}
	}

	private  void connect(){
		try {
			if (doEnableSSL){
				initConnectionWithSSL();
			}else {
				initConnection();
			}
			if (socket.isConnected()){
				socket.setSoTimeout(timeOut);
				reader = new SmtpReader(socket);
				writer = new SmtpWriter(socket);
				onConnect(reader.getSmtpResponse());
			}
		} catch (IOException e) {
			onError(e);
		}
	}

	private void initConnection()throws IOException{
		socket = new Socket(smtpHost, smtpPort);
	}

	private void initConnectionWithSSL() throws IOException {
		socket = SSLSocketFactory.getDefault().createSocket(smtpHost,smtpPort);
	}

	@Override
	public void onConnect(SmtpResponse response) {
		if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Service_Ready){
			helo();
		}
		else onServerError(response);
	}

	private   void helo(){
		String command = COMMAND_HELO + smtpHost + COMMAND_END;
		try {
			writer.write(command);
			onHeloResponse(reader.getSmtpResponse());
		} catch (IOException e) {
			onError(e);
		}
	}

	@Override
	public void onHeloResponse(SmtpResponse response) {
		if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Action_OK){
			if (initialState && doStartTLS){
				initialState = false;
				setStartTLS();
			}
			else if (isUseAuth){
				setAuthenticationMechanism(authentication);
			}
			else {
				if (message == null){
					onError(new IOException("Message Object must be Not Null"));
				}
				else mailFrom(message.getFrom());
			}
		}
		else onServerError(response);
	}

	private void setStartTLS(){
		String command = COMMAND_START_TLS + COMMAND_END;
		try {
			writer.write(command);
			onStartTLSResponse(reader.getSmtpResponse());
		} catch (IOException e) {
			onError(e);
		}
	}

	@Override
	public void onStartTLSResponse(SmtpResponse response) {
		if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Service_Ready){
			starTLS();
		}
		else onServerError(response);
	}

	private void starTLS(){
		try {
			SSLSocket sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(
					socket,
					socket.getInetAddress().getHostAddress(),
					socket.getPort(),
					true);
			sslSocket.setSoTimeout(timeOut);
			sslSocket.setEnableSessionCreation(true);

			socket = sslSocket;
			writer = new SmtpWriter(socket);
			reader = new SmtpReader(socket);

			onTLSStarted();
		}catch (IOException e){
			onError(e);
		}
	}

	@Override
	public void onTLSStarted() {
		helo();
	}

	private void setAuthenticationMechanism(Authentication authentication){
		if (authentication == null){
			onError(new IOException("Authentication Object must be No Null"));
		}
		else {
			Authentication.AuthCommand command = authentication.getAuthenticationCommand();
			try {
				authentication.setAuthenticationMechanism(command, writer,reader,this);
			} catch (IOException e) {
				onError(e);
			}
		}
	}

	@Override
	public void onAuthenticationMechanismResponse(SmtpResponse response) {
		if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Authentication_Mechanism_Accepted){
			setAuthentication(authentication);
		}
		else onServerError(response);
	}

	private void setAuthentication(Authentication authentication){
		if (authentication == null){
			onError(new IOException("Authentication Object must be No Null"));
		}
		else {
			Authentication.AuthCommand command = authentication.getAuthenticationCommand();
			try {
				authentication.authenticate(command, writer,reader,this);
			} catch (IOException e) {
				onError(e);
			}
		}
	}

	@Override
	public void onAuthenticateResponse(SmtpResponse response) {
		if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Authentication_Successful){
			if (message == null){
				onError(new IOException("Message Object must be Not Null"));
			}
			else  mailFrom(message.getFrom());
		}
		else onServerError(response);
	}

	private void mailFrom(String mail){
		if (mail  == null){
			onError(new IOException("Sender mail must be Not Null"));
		}
		else {
			String command = COMMAND_MAIL_FROM +"<"+mail +">"+ COMMAND_END;
			try {
				writer.write(command);
				onMailFromResponse(reader.getSmtpResponse());
			} catch (IOException e) {
				onError(e);
			}
		}
	}

	@Override
	public void onMailFromResponse(SmtpResponse response) {
		if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Action_OK){
			mailTo(message.getRecipient());
		}
		else onServerError(response);
	}

	private void mailTo(String to){
		if (to == null){
			onError(new IOException("Recipient mail must be Not Null"));
		}
		else {
			String command = COMMAND_RECIPIENT_TO   +"<"+ to +">"+ COMMAND_END;
			try {
				writer.write(command);
				onRecipientToResponse(reader.getSmtpResponse());
			} catch (IOException e) {
				onError(e);
			}
		}
	}

	@Override
	public void onRecipientToResponse(SmtpResponse response) {
		if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Action_OK){
			dataTransReqPermission();
		}
		else onServerError(response);
	}

	private void dataTransReqPermission(){
		String command = COMMAND_DATA + COMMAND_END ;
		try {
			writer.write(command);
			onDataRequestPermission(reader.getSmtpResponse());
		} catch (IOException e) {
			onError(e);
		}
	}

	@Override
	public void onDataRequestPermission(SmtpResponse response) {
		if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Mail_Transfer_Permission_Accepted){
			if (message == null){
				onError(new IOException("Message Object must be Not Null"));
			}
			else setData(message);
		}
		else onServerError(response);
	}

	private void setData(Message message){
		try {
			message.sendMessage(writer,reader,this);
		} catch (IOException e) {
			onError(e);
		}
	}

	@Override
	public void onDataResponse(SmtpResponse response) {
		if (response.getResponseCode() == SmtpResponse.RESPONSE_CODE.Action_OK){
			onSuccess();
		}
		else onServerError(response);
	}

	private void close(){
		String command = COMMAND_QUIT+COMMAND_END;
		try {
			writer.write(command);
			onCloseResponse(reader.getSmtpResponse());
		} catch (IOException e) {
			onError(e);
		}
	}

	@Override
	public void onCloseResponse(SmtpResponse response) {
		release();
	}

	private void release(){
		try {
			if (reader != null) reader.close();
			if (writer != null) writer.close();
			if (socket != null) socket.close();
		} catch (IOException ignored){}
	}

	@Override
	public void onSuccess() {
		if (listener != null){
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					listener.onSendSuccess();
				}
			});
		}
		close();
	}

	@Override
	public void onServerError(final SmtpResponse response) {
		if (listener != null)
			mainHandler.post(new Runnable() {
				@Override
				public void run() {
					listener.onServerError(response.getResponseCode(),response.getResponseMessage());
				}
			});
		close();
	}

	@Override
	public void onError(final IOException e) {
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				if (listener != null) listener.onError(e);
			}
		});
		release();
	}

	public interface OnCompleteListener {

		void onSendSuccess();

		void onServerError(int responseCode, String responseMessage);

		void onError(Exception e);
	}
}
