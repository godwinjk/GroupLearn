package com.grouplearn.project.app.file;

import android.graphics.Bitmap;
import android.os.Environment;

import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.utilities.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Godwin on 09-05-2017 14:28 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class FileManager {
    public static final String DIRECTORY_NAME = "GroupLearn";
    public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + "/" + DIRECTORY_NAME;

    public static final String VIDEO_FOLDER_PATH = ROOT_PATH + "/Video";
    public static final String SENT_VIDEO_FOLDER_PATH = VIDEO_FOLDER_PATH + "/Sent";

    public static final String IMAGE_FOLDER_PATH = ROOT_PATH + "/Images";
    public static final String SENT_IMAGE_FOLDER_PATH = IMAGE_FOLDER_PATH + "/Sent";

    public static final String DOCUMENT_FOLDER_PATH = ROOT_PATH + "/Doc";
    public static final String SENT_DOCUMENT_FOLDER_PATH = DOCUMENT_FOLDER_PATH + "/Sent";


    public FileManager() {
        createDirectory();
    }

    public void createDirectory() {
        File rootDirectory = new File(ROOT_PATH);
        if (!rootDirectory.exists())
            rootDirectory.mkdirs();
        File videoDirectory = new File(VIDEO_FOLDER_PATH);
        if (!videoDirectory.exists())
            videoDirectory.mkdirs();
        File videoSentDirectory = new File(SENT_VIDEO_FOLDER_PATH);
        if (!videoSentDirectory.exists())
            videoSentDirectory.mkdirs();
        File imageDirectory = new File(IMAGE_FOLDER_PATH);
        if (!imageDirectory.exists())
            imageDirectory.mkdirs();
        File imageSentDirectory = new File(SENT_IMAGE_FOLDER_PATH);
        if (!imageSentDirectory.exists())
            imageSentDirectory.mkdirs();
        File docDirectory = new File(DOCUMENT_FOLDER_PATH);
        if (!docDirectory.exists())
            rootDirectory.mkdirs();
        File docSentDirectory = new File(SENT_DOCUMENT_FOLDER_PATH);
        if (!docSentDirectory.exists())
            docSentDirectory.mkdirs();
    }

    public File copyToLocal(File file, int type) {
        InputStream in = null;
        OutputStream out = null;
        File outPutFileDir = getSentDirectory(type);
        File outPutFile = new File(outPutFileDir, getFileName(file.getName()));

        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(outPutFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            out.flush();
            out.close();
        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        return outPutFile;
    }

    private String getFileName(String fileName) {
        String tempFileName = System.currentTimeMillis() / 1000 + "";
        return (tempFileName + fileName);
    }

    public File getSentDirectory(int action) {
        File file = new File(ROOT_PATH);
        if (action == GLMessage.DOCUMENT) {
            file = new File(SENT_DOCUMENT_FOLDER_PATH);
        } else if (action == GLMessage.VIDEO) {
            file = new File(SENT_VIDEO_FOLDER_PATH);
        } else if (action == GLMessage.IMAGE) {
            file = new File(SENT_IMAGE_FOLDER_PATH);
        }
        return file;
    }

    public File getDirectory(int action) {
        File file = new File(ROOT_PATH);
        if (action == GLMessage.DOCUMENT) {
            file = new File(DOCUMENT_FOLDER_PATH);
        } else if (action == GLMessage.VIDEO) {
            file = new File(VIDEO_FOLDER_PATH);
        } else if (action == GLMessage.IMAGE) {
            file = new File(IMAGE_FOLDER_PATH);
        }
        return file;
    }

    public void saveBitmap(String name, Bitmap resource) {
        FileOutputStream out = null;
        try {
            File file = new File(IMAGE_FOLDER_PATH, getFileName(name));
            out = new FileOutputStream(file);
            resource.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
