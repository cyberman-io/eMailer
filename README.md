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
	
1. create a new SmtpSession:

 		SmtpSession session = new SmtpSession(smtp_host_address);
 
 	or:
 
 		SmtpSession session = new SmtpSession(smtp_host_address,smtp_host_prot);
 
 	ex:
 
 		SmtpSession session = new SmtpSession("smtp.yandex.com",465);
 
 
