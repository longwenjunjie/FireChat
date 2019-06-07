package com.yunliaoim.firechat.util;

import com.yunliaoim.firechat.R;
import com.yunliaoim.firechat.utils.util.UIUtil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

/**
 *
 */
public class ShareHelper {

    public static void shareApp(Context context) {
//        String packageName = context.getPackageName();
//        try {
//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
//            File sourceFile=new File(packageInfo.applicationInfo.sourceDir);
            //调用android系统的分享窗口
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "https://github.com/longwenjunjie/FireChat");
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_app_title)));
//        } catch (PackageManager.NameNotFoundException e) {
//            UIUtil.showToast(context, "分享失败");
//        }
    }
}
