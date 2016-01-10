package avenwu.net.filelocker;

import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by chaobin on 1/10/16.
 */
public class ToolKit {
    public static TranslateTask encodeFile(File src, File des, OnProgressUpdateListener listener) {
        TranslateTask task = new TranslateTask(listener) {
            @Override
            byte translate(byte c) {
                return (byte) (c + '1');
            }
        };
        AsyncTaskCompat.executeParallel(task, src, des);
        return task;
    }

    public static TranslateTask decodeFile(File src, File des, OnProgressUpdateListener listener) {
        TranslateTask task = new TranslateTask(listener) {
            @Override
            byte translate(byte c) {
                return (byte) (c - '1');
            }
        };
        AsyncTaskCompat.executeParallel(task, src, des);
        return task;
    }

    public interface OnProgressUpdateListener {
        void onProgress(long bytes, float percent);

        void onComplete(Result result);
    }

    public static class Result {
        boolean success;
        String msg;

        public Result(boolean success, String msg) {
            this.success = success;
            this.msg = msg;
        }
    }

    public static abstract class TranslateTask extends AsyncTask<File, Long, Result> {
        OnProgressUpdateListener mListener;

        public TranslateTask(OnProgressUpdateListener listener) {
            mListener = listener;
        }

        @Override
        protected Result doInBackground(File... params) {
            Result result;
            if (params == null || params.length < 2 || params[0] == null || params[1] == null) {
                result = new Result(false, "File can not be null");
            } else {
                File src = params[0];
                File des = params[1];
                InputStream inputStream = null;
                FileOutputStream outputStream = null;
                try {
                    inputStream = new FileInputStream(src);
                    outputStream = new FileOutputStream(des);
                    byte[] buffer = new byte[1024 * 8];
                    int length = 0;
                    long timestamp = System.currentTimeMillis();
                    long totalLength = src.length();
                    long readLength = 0;
                    try {
                        while ((length = inputStream.read(buffer)) > 0) {
                            for (int i = 0; i < length; i++) {
                                byte c = buffer[i];
                                byte d = translate(c);
                                buffer[i] = d;
                            }
                            outputStream.write(buffer, 0, length);
                            readLength += length;
                            if (System.currentTimeMillis() - timestamp >= 1000) {
                                timestamp = System.currentTimeMillis();
                                publishProgress(readLength, totalLength);
                            }
                        }
                        outputStream.close();
                        inputStream.close();
                        result = new Result(true, "Complete");
                    } catch (IOException e) {
                        e.printStackTrace();
                        result = new Result(false, "IO exception while reading file");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    result = new Result(false, "File can not be found");
                }
            }
            return result;
        }

        abstract byte translate(byte c);

        @Override
        protected void onProgressUpdate(Long... values) {
            if (mListener != null) {
                mListener.onProgress(values[0], values[0] / (float) values[1]);
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            if (mListener != null) {
                mListener.onComplete(result);
            }
        }
    }
}
