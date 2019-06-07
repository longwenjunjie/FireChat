package com.yunliaoim.firechat;

import com.yunliaoim.firechat.util.AppFileHelper;
import com.yunliaoim.firechat.utils.app.AppContext;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orhanobut.logger.Logger;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;

public class IMApp extends Application  {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        AppContext.init(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        UnCaughtCrashExceptionHandler handler = UnCaughtCrashExceptionHandler.getInstance();
//        handler.init(this);
//        handler.setLogPath(AppFileHelper.getAppCrashDir());

        Logger.init("Smack");
        initImageLoader();

        LitePal.initialize(this);
    }

    private void initImageLoader() {
        File cacheDir = new File(AppFileHelper.getAppImageCacheDir());
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        try {
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .threadPoolSize(3)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                    .diskCache(new LruDiskCache(cacheDir, new Md5FileNameGenerator(), 50 * 1024 * 1024))
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
                    .diskCacheFileCount(100)
                    .writeDebugLogs()
                    .build();
            ImageLoader.getInstance().init(config);
        } catch (IOException e) {
            Logger.e(e, "ImageLoader init failure");
        }
    }

}
