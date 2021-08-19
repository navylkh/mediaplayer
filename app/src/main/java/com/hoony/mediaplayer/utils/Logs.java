package com.hoony.mediaplayer.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hoony.mediaplayer.BuildConfig;

import java.util.Locale;

public class Logs {
    private static final String MSG_NULL = "<NULL>";
    private static final String MSG_ERROR = "<ERROR>";


    private static final String TAG = "";

    private static final int STACK_TRACE_LEVELS = 7;

    private static final int MAX_LOG_LENGTH = 1000;

    private static boolean isDebug = BuildConfig.DEBUG;


    public enum LogLevel {
        v, d, i, w, e
    }

    public static void v(Object... msgs) {
        setLog(LogLevel.v, msgs);
    }

    public static void d(Object... msgs) {
        setLog(LogLevel.d, msgs);
    }

    public static void i(Object... msgs) {
        setLog(LogLevel.i, msgs);
    }

    public static void w(Object... msgs) {
        setLog(LogLevel.w, msgs);
    }

    public static void e(Object... msgs) {
        setLog(LogLevel.e, msgs);
    }


    private static void setLog(LogLevel logLevel, Object... msgs) {
        if (!isDebug) {
            return;
        }


        StringBuilder msgString = new StringBuilder();
        for (Object msg : msgs) {
            if (msg == null) {
                msgString.append(MSG_NULL);
            }
            if (msg instanceof byte[]) {
                msgString.append(byteArrayToString((byte[]) msg));
            } else if (msg instanceof Intent) {
                msgString.append(intentToString((Intent) msg));
            } else if (msg instanceof Exception) {
                msgString.append(android.util.Log.getStackTraceString((Exception) msg));
            } else {
                msgString.append(msg);
            }
            msgString.append(", ");
        }

        try {
            msgString = new StringBuilder(msgString.substring(0, msgString.length() - 2));
        } catch (Exception e) {
            msgString = new StringBuilder(MSG_ERROR);
        }

        int logLen = msgString.length();
        if (logLen > MAX_LOG_LENGTH) {
            int chunkCount = logLen / MAX_LOG_LENGTH;

            for (int i = 0; i <= chunkCount; i++) {
                int max = MAX_LOG_LENGTH * (i + 1);
                if (max >= logLen) {
                    log(logLevel, msgString.substring(MAX_LOG_LENGTH * i));
                } else {
                    log(logLevel, msgString.substring(MAX_LOG_LENGTH * i, max));
                }
            }
        } else {
            log(logLevel, msgString.toString());
        }
    }

    private static void log(LogLevel l, String logMessage) {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            switch (l) {
                case v:
                    android.util.Log.v(TAG, getClassNameMethodNameAndLineNumber() + logMessage);
                    break;
                case d:
                    android.util.Log.d(TAG, getClassNameMethodNameAndLineNumber() + logMessage);
                    break;
                case i:
                    android.util.Log.i(TAG, getClassNameMethodNameAndLineNumber() + logMessage);
                    break;
                case w:
                    android.util.Log.w(TAG, getClassNameMethodNameAndLineNumber() + logMessage);
                    break;
                case e:
                    android.util.Log.e(TAG, getClassNameMethodNameAndLineNumber() + logMessage);
                    break;
                default:
                    break;
            }
        } else {
            if (stackTrace != null) {
                android.util.Log.e(" ", stackTrace[2].toString());
            }
            android.util.Log.d(TAG, logMessage);
        }
    }

    private static String byteArrayToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        sb.append("[ ");
        for (byte b : bytes) {
            sb.append(String.format(Locale.US, "%02X ", b & 0xff));
        }
        sb.append("]");
        return sb.toString();
    }


    private static String intentToString(Intent i) {
        StringBuilder msgString = new StringBuilder("ACTION : " + i.getAction());

        Bundle bundle = i.getExtras();

        if (bundle == null) {
            msgString.append("\n       <NO BUNDLE>");
        } else {
            for (String key : bundle.keySet()) {
                msgString.append("\n    ").append(key).append(" = ").append(bundle.get(key));
            }
        }
        return msgString.toString();
    }


    private static String getClassNameMethodNameAndLineNumber() {
        return ".(" + getFileName() + ":" + getLineNumber() + ") " + getMethodName() + "(): ";
    }

    private static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS].getLineNumber();
    }

    private static String getClassName() {
        String fileName = Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS].getFileName();
        return fileName.substring(0, fileName.length() - 5);
    }

    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS].getMethodName();
    }

    private static String getFileName() {
        return Thread.currentThread().getStackTrace()[STACK_TRACE_LEVELS].getFileName();
    }
}
