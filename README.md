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

	set message data:

		message.setSubject(subject);
        	message.setText(messageText);
        	message.setSenderName(optional_name);
		
		
