package avenwu.net.filelocker;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testTextEncode() throws Exception {
        InputStream inputStream = getContext().getResources().getAssets().open("sample_text.txt");
        File desFile = new File(getContext().getExternalCacheDir(), "sample_text.txt.xyz");
        FileOutputStream outputStream = new FileOutputStream(desFile);

        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        while ((length = inputStream.read(buffer)) > 0) {
            for (int i = 0; i < length; i++) {
                byte c = buffer[i];
                byte d = encode(c);
                buffer[i] = d;
            }
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        assertTrue(desFile.exists());
        assertTrue(desFile.length() > 0);
    }

    public void testTextDecode() throws Exception {
        File desFile = new File(getContext().getExternalCacheDir(), "sample_text.txt.xyz");
        FileInputStream fileInputStream = new FileInputStream(desFile);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        while ((length = fileInputStream.read(buffer)) > 0) {
            for (int i = 0; i < length; i++) {
                byte c = buffer[i];
                byte d = decode(c);
                buffer[i] = d;
            }
            outputStream.write(buffer, 0, length);
        }
        String decodedText = outputStream.toString();
        fileInputStream.close();
        outputStream.close();

        InputStream inputStream = getContext().getResources().getAssets().open("sample_text.txt");
        outputStream = new ByteArrayOutputStream();
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        String originText = outputStream.toString();
        assertEquals(originText, decodedText);
    }

    byte encode(byte c) {
        return (byte) (c + '1');
    }

    byte decode(byte c) {
        return (byte) (c - '1');
    }

    public void testVideoEncode() throws Exception {
        InputStream inputStream = getContext().getResources().getAssets().open("sample_video.mp4");
        File desFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES), "sample_video.mp4.xyz");
        Log.d("VIDEO_ENCODE", desFile.getAbsolutePath());
        FileOutputStream outputStream = new FileOutputStream(desFile);

        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        while ((length = inputStream.read(buffer)) > 0) {
            for (int i = 0; i < length; i++) {
                byte c = buffer[i];
                byte d = encode(c);
                buffer[i] = d;
            }
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        assertTrue(desFile.exists());
        assertTrue(desFile.length() > 0);
    }

    public void testVideoDecode() throws Exception {
        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES), "sample_video.mp4.xyz");
        FileInputStream inputStream = new FileInputStream(file);
        File desFile = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_MOVIES), "sample_video_decoded.mp4");
        FileOutputStream outputStream = new FileOutputStream(desFile);
        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        while ((length = inputStream.read(buffer)) > 0) {
            for (int i = 0; i < length; i++) {
                byte c = buffer[i];
                byte d = decode(c);
                buffer[i] = d;
            }
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        assertTrue(desFile.exists());
        assertTrue(desFile.length() > 0);

    }
}