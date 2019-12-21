package com.cyberman.emailer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class AttachmentMessage extends PlainTextMessage {

    private static final int BOUNDARY_SIZE_DEFAULT = 16;
    private final static  int ATTACHMENTS_MAX_SIZE = 1024*1024*10; //10M bytes;
    private int attachmentsSize = 1024*1024*4 ;//4M bytes;

    private List<Attachment> attachments ;
    private String messageBoundary;

    public AttachmentMessage(String from, String recipient, List<Attachment> attachments) {
        super(from, recipient);
        this.attachments = attachments;
    }

    public AttachmentMessage(String from, String recipient, Attachment... attachments) {
        super(from, recipient);
       this.attachments = Arrays.asList(attachments);
    }

    @Override
    protected void sendMessage(SmtpWriter writer, SmtpReader reader, SmtpListener listener) throws IOException{
        if (getAttachmentsSize() > attachmentsSize){
            listener.onError(new IOException(
                    String.format(Locale.US,"Files size must be less then %d bytes",attachmentsSize))
            );
            return;
        }
        String nLine = "\n";
        writer.write(
                "Date: "+getDate()+ nLine +
                        "From: "+getSenderName()+"<"+getFrom()+">"+ nLine +
                        "To: "+getRecipient()+ nLine +
                        "Subject: "+getSubject()+ nLine);

        writer.write(
                "MIME-Version: 1.0"+ nLine +
                        "Content-Type: multipart/mixed;boundary=\""+getStringBoundary()+"\""+ nLine + nLine);

        // message Text
        writer.write(
                "--"+getStringBoundary()+ nLine +
                        "Content-Type: text/plain; charset=\"iso-8859-1\""+
                        "Content-Transfer-Encoding: quoted-printable"+ nLine + nLine);

        writer.write(getText()+ nLine + nLine);

        //add Attachments.
        char[] chars = new char[1024*4] ;//8k bytes.
        int count;
        for (Attachment attachment : attachments) {
            writer.write(
                    "--"+getStringBoundary()+ nLine +
                            "Content-Transfer-Encoding: base64"+ nLine +
                            "Content-Type: "+attachment.getMimeType()+" ; name=\'"+attachment.getFileName()+"\""+ nLine +
                            "Content-Disposition: attachment ; filename=\'"+attachment.getFileName()+"\""+ nLine + nLine);

            //file content in base64.
            String base64File = attachment.getFileContent();
            StringReader sr = new StringReader(base64File);
            while ((count = sr.read(chars)) > 0){
                writer.write(new String(chars,0,count));
            }
            writer.write("\n\n");
        }

        writer.write("--"+getStringBoundary()+"--"+ nLine);

        writer.write(".\r"+ nLine);

        listener.onDataResponse(reader.getSmtpResponse());
    }

    private String getStringBoundary() {
        if (messageBoundary == null){
            messageBoundary = generateStringBoundary(BOUNDARY_SIZE_DEFAULT);
            return messageBoundary;
        }else {
            return messageBoundary;
        }
    }

    private String generateStringBoundary(int size){
        String s = "abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder generateMessage = new StringBuilder();
        for (int i = 0;i<size;i++){
            char c = s.charAt(new Random().nextInt(s.length()));
            generateMessage.append(c);
        }
        return generateMessage.toString();
    }

    private long getAttachmentsSize(){
        long size = 0;
        for (Attachment a : attachments) {
           size += a.getFileSize();
        }
        return size;
    }

    public void setAttachmentsMaxSize(int sizeInByte){
        if (sizeInByte < ATTACHMENTS_MAX_SIZE){
            attachmentsSize = sizeInByte;
        }
        else {
            attachmentsSize = ATTACHMENTS_MAX_SIZE;
        }
    }

}
