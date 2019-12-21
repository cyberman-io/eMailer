package com.cyberman.emailer;

import android.util.Base64;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Attachment  {

    private File file;

    public Attachment(File file) {
        this.file = file;
    }

    public Attachment(String filePath) {
        file = new File(filePath);
    }

    String getMimeType(){
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getPath());
      return   MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    String getFileName(){
        return file.getName();
    }

    long getFileSize(){
        return  file.length();
    }

    String getFileContent() {
        byte[] bytes = new byte[(int) file.length()];
        try{
            FileInputStream is = new FileInputStream(file);
            is.read(bytes);
            is.close();
            return Base64.encodeToString(bytes,Base64.NO_WRAP);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
