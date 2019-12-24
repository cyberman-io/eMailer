# eMailer

**Android Library to send eMails.**

1. Add it in your root build.gradle at the end of repositories:

		allprojects {
			repositories {
				...
				maven { url 'https://jitpack.io' }	
			}
		}

2.  Add the dependency:
  
  		dependencies {
	        	implementation 'com.github.cyberman-io:eMailer:1.0.0'
		}

3. create a new SmtpSession:

 		SmtpSession session = new SmtpSession(smtp_host_address);
 
 	or:
 
 		SmtpSession session = new SmtpSession(smtp_host_address,smtp_host_prot);
 
 	ex:
 
 		SmtpSession session = new SmtpSession("smtp.yandex.com",465);

4. enable encryption protocol:

		session.enableStartTLS();

	or:

		session.enableSSL();

5. set authentication mechanisme and authentication info:

		session.useAuthentication(new LoginAuthentication(user_name,user_password));

	or:

		session.useAuthentication(new PlainAuthentication(user_name,user_password));

6. create a new Message Object:

		PlainTextMessage message = new PlainTextMessage(sender_email,recipient_email);
		 
	or if you want to add an attachments (files):
	
		Attachment attachment = new Attachment(file_path);
		
		AttachmentMessage message = new AttachmentMessage(sender_email,recipient_email,attachment);

	set message data:

		message.setSubject(subject);

        	message.setText(messageText);

        	message.setSenderName(optional_name);
		
7. set message to session:

		session.setMessage(message);

8. set OnCompleteListener:
		
		session.setOnCompleteListener(new SmtpSession.OnCompleteListener() {
            		@Override
            		public void onSendSuccess() {
				//your code.
            		}

            		@Override
            		public void onServerError(int responseCode, String responseMessage) {
				//your code.
            		}

            		@Override
            		public void onError(Exception e) {
				//your code.
            		}
        	});

9. send message:

		  session.send();

