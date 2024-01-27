package androidx.top.hyperos.dynamic.notify;

import android.app.StatusBarManager;
import android.content.Context;
import android.os.Bundle;

import androidx.top.hyperos.dynamic.notify.ext.Config;
import androidx.top.hyperos.dynamic.notify.ext.StatusBarGuideModel;
import androidx.top.hyperos.dynamic.notify.ext.Tools;


import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class StrongToastInfo {
    public StrongToastInfo(Context context, StatusBarGuideModel statusBarGuideModel) {
        toast(context, statusBarGuideModel, 3500);
    }

    public StrongToastInfo(Context context, StatusBarGuideModel statusBarGuideModel, long duration) {
        toast(context, statusBarGuideModel, duration);
    }
    
    public static class status {
        public static String SHOW_CUSTOM = "show_custom_strong_toast";
        public static String HIDE = "hide_strong_toast";
        public static String UPDATE = "update_strong_toast";
        public static String SHOW_DEFAULT = "show_default_strong_toast";
    }

    public static class model {
        public static String VIDEO_TEXT = "video_text";
        public static String VIDEO_BITMAP_INTENT = "video_bitmap_intent";
        public static String TEXT_BITMAP = "text_bitmap";
        public static String TEXT_BITMAP_INTENT = "text_bitmap_intent";
        public static String VIDEO_TEXT_TEXT_VIDEO = "video_text_text_video";
    }

    public static class format {
        public static String PNG = "png";
        public static String SVG = "SVG";
        public static String MP4 = "mp4";
    }

    private void toast(Context context, StatusBarGuideModel statusBarGuideModel, long duration) {
        String param = Tools.toJson(statusBarGuideModel);
        Bundle data = new Bundle();
        data.putString("package_name", context.getPackageName());
        data.putString("status_bar_strong_toast", status.SHOW_CUSTOM);
        data.putString("strong_toast_category", model.TEXT_BITMAP);
        data.putLong("duration", duration);
        data.putBoolean("target", true);
        data.putString("param", param);
        StatusBarManager status = (StatusBarManager) context.getSystemService(Context.STATUS_BAR_SERVICE);
        try {
            Method setStatus = status.getClass().getMethod("setStatus", int.class, String.class, Bundle.class);
            setStatus.invoke(status, 1, "strong_toast_action", data);
        } catch (Exception e) {
        }
    }
}
