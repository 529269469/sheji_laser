package com.example.laser.utils;

import android.app.Application;

import androidx.core.content.FileProvider;


/**
 * Author      : silveryTitan
 * Email       : silverytitan94@gmail.com
 * Date        : on 2021-08-26 16:52.
 * Description :
 */
public class AppFileProvider extends FileProvider {
    @Override
    public boolean onCreate() {
        return true;
    }
}