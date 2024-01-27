package androidx.top.hyperos.dynamic.notify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.top.hyperos.dynamic.notify.ext.Config;
import androidx.top.hyperos.dynamic.notify.ext.StatusBarGuideModel;
import androidx.top.hyperos.dynamic.notify.ext.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookInit implements IXposedHookLoadPackage {
    private TextView mLeftTextView;
    private TextView mRightTextView;
    private ImageView mRightImageView;
    private RelativeLayout mRLRight;
    private LoadPackageParam mLpparam;
    private Context context;
    private XSharedPreferences sp;
    private XC_MethodHook methodHook;
    private XC_MethodHook.Unhook Hook;

    public static Class<?> getClass(XC_LoadPackage.LoadPackageParam lpparam, String classname) {
        try {
            return lpparam.classLoader.loadClass(classname);
        } catch (ClassNotFoundException e) {
            XposedBridge.log(e);
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> findClass(String classname) {
        return getClass(mLpparam, classname);
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        String packageName = lpparam.packageName;
        this.mLpparam = lpparam;
        if (packageName.equals(Config.SystemUiPackage)) {
            sp = getSharedPreferences(Tools.DEFAULT_STORAGE);
            initContext();
            findNotificat();
        }
    }

    private void initContext() {
        XposedHelpers.findAndHookMethod(
                findClass(Config.DaggerReferenceGlobalRootComponentPackage),
                "displayIdInteger",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        context = (Context) XposedHelpers.getObjectField(param.thisObject, "context");
                    }
                });
    }

    private void findNotificat() {
        XposedHelpers.findAndHookMethod(
                findClass(Config.NotificationListenerPackage),
                "onNotificationPosted",
                StatusBarNotification.class,
                NotificationListenerService.RankingMap.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        StatusBarNotification sbn = (StatusBarNotification) param.args[0];
                        //NotificationListenerService.RankingMap rankingMap = (NotificationListenerService.RankingMap) param.args[1];
                        Notification notification = sbn.getNotification();
                        String packageName = sbn.getPackageName();
                        //boolean isOngoing = (notification.flags & Notification.FLAG_ONGOING_EVENT) != 0;
                        if (notification != null) {
                            Bundle data = notification.extras;
                            CharSequence title = data.getCharSequence(Notification.EXTRA_TITLE); // title
                            CharSequence text = data.getCharSequence(Notification.EXTRA_TEXT); // text
                            Icon icon = notification.getLargeIcon();
                            if (icon == null) {
                                icon = notification.getSmallIcon();
                            }
                            if (text != null && icon != null) {
                                boolean shield = false;
                                boolean music = false;
                                long shortTime = 3500;
                                long longTime = 5500;
                                String intercept = null;
                                String input = text.toString();
                                if (sbn.getPackageName().equals(Config.xmsf)) {
                                    input = input.replaceFirst("•", ":");
                                    input = title.toString() + input;
                                }
                                if (sp.contains(packageName)) {
                                    String jsonString = sp.getString(packageName, "");
                                    try {
                                        JSONObject json = new JSONObject(jsonString);
                                        shield = json.getBoolean("shield");
                                        music = json.getBoolean("music");
                                        shortTime = json.getLong("short");
                                        longTime = json.getLong("long");
                                        intercept = json.getString("intercept");
                                    } catch (JSONException e) {
                                        XposedBridge.log(e.toString());
                                    }
                                }
                                long duration = input.length() > 18 ? longTime : shortTime;
                                if (!shield && !checkIfStartsWith(input, intercept) && !input.contains("GroupSummary")) {
                                    CharSequence tickerText = notification.tickerText;
                                    if (music && tickerText != null) {
                                        input = tickerText.toString();
                                    }
                                    if (Hook != null) {
                                        Hook.unhook();
                                    }
                                    if (sp.getBoolean("log", false)) {
                                        HashMap<String, String> print = new HashMap<String, String>();
                                        print.put("content", input);
                                        print.put("name", packageName);
                                        XposedBridge.log(Tools.concat(" [WENYI] :", print.toString()));
                                    }
                                    start(context, input, duration);
                                    initToast(notification, icon, input);
                                }
                            }
                        }
                    }
                });
    }

    public static boolean checkIfStartsWith(String input, String intercept) {
        if (intercept == null || intercept.isEmpty()) {
            return false;
        }
        if (!intercept.contains("|")) {
            return input.startsWith(intercept.trim());
        } else {
            String[] segments = intercept.split("\\|");
            for (String segment : segments) {
                if (!segment.trim().isEmpty() && input.startsWith(segment.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static XSharedPreferences getSharedPreferences(String key) {
        XSharedPreferences sp = new XSharedPreferences(Config.AppPackage, key);
        sp.makeWorldReadable();
        return sp;
    }

    private void start(Context context, String str, long duration) {
        StatusBarGuideModel bar = new StatusBarGuideModel();
        StatusBarGuideModel.TextParams text = new StatusBarGuideModel.TextParams();
        text.setText(str);
        text.setTextColor(-1);

        StatusBarGuideModel.Left left = new StatusBarGuideModel.Left();
        left.setTextParams(text);
        StatusBarGuideModel.IconParams icon = new StatusBarGuideModel.IconParams();
        icon.setCategory("drawable");
        icon.setIconResName("ic_device_water_off");
        icon.setIconFormat("svg");
        icon.setIconType(1);

        StatusBarGuideModel.Right right = new StatusBarGuideModel.Right();
        right.setIconParams(icon);
        bar.setLeft(left);
        bar.setRight(right);
        new StrongToastInfo(context, bar, duration);
    }

    private void initToast(Notification notification, Icon icon, String text) {
        methodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Bitmap draw = Tools.drawableToBitamp(icon.loadDrawable(context));
                mLeftTextView = (TextView) XposedHelpers.getObjectField(param.thisObject, "mLeftTextView");
                mRightTextView = (TextView) XposedHelpers.getObjectField(param.thisObject, "mRightTextView");
                mRightImageView = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mRightImageView");
                mRLRight = (RelativeLayout) XposedHelpers.getObjectField(param.thisObject, "mRLRight");
                mLeftTextView.setText(text);
                mLeftTextView.setTextSize(13);
                mRightTextView.setTextSize(13);
                mRightImageView.setImageBitmap(Tools.getCroppedBitmap(draw));
                AlphaAnimation anim = new AlphaAnimation(0, 1);
                anim.setDuration(300);
                mLeftTextView.setAnimation(anim);
                mRightTextView.setAnimation(anim);
                mRightImageView.setAnimation(anim);
                anim.start();
                mRLRight.setOnClickListener(v -> {
                    PendingIntent intent = notification.contentIntent;
                    try {
                        intent.send();
                    } catch (PendingIntent.CanceledException e) {

                    }
                });
                XposedBridge.unhookMethod(param.method, this);
            }
        };

        Hook = XposedHelpers.findAndHookMethod(findClass(Config.ToastPackage), "setValue", methodHook);  // 进行新的 hook
    }

}
