package com.xuwanjin.inchoate;

import android.os.Build;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashReportWriter implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashReportWriter";
    private final Thread.UncaughtExceptionHandler defaultHandler;

    public static File getFile() {
        return new File("crash-report.log");
    }

    public CrashReportWriter() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        File path = getFile();
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream(path));
            out.println("## Crash info");
            out.println("Time: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
            out.println("Inchoate version: 1.0" );
            out.println();
            out.println("## StackTrace");
            throwable.printStackTrace(out);
            out.println("```");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }finally {
            if (out != null){
                out.close();
            }
        }
        defaultHandler.uncaughtException(thread, throwable);
    }
    public static String getSystemInfo() {
        return "## Environment"
                + "\nAndroid version: " + Build.VERSION.RELEASE
                + "\nOS version: " + System.getProperty("os.version")
                + "\nModel: " + Build.MODEL
                + "\nDevice: " + Build.DEVICE
                + "\nProduct: " + Build.PRODUCT;
    }
}
