package androidx.top.hyperos.dynamic.notify;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.top.hyperos.dynamic.notify.ext.Config;
import androidx.top.hyperos.dynamic.notify.ext.StatusBarGuideModel;
import androidx.top.hyperos.dynamic.notify.ext.Tools;

import org.json.JSONException;
import org.json.JSONObject;

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
                            CharSequence input = notification.tickerText;
                            if (input != null && context != null) {
                                Boolean shield = false;
                                long shortTime = 3500; // 默认短时间
                                long longTime = 5500; // 默认长时间
                                String intercept = "";
                                Long duration;
                                String text = input.toString();
                                int firstColonIndex = text.indexOf(":");
                                if (firstColonIndex != -1 && packageName.equals(Config.qq)) {
                                    text = text.substring(firstColonIndex + 1).trim();
                                }
                                if (sp.contains(packageName)) {
                                    String jsonString = sp.getString(packageName, "");
                                    try {
                                        JSONObject json = new JSONObject(jsonString);
                                        shield = json.getBoolean("shield");
                                        shortTime = json.getLong("short");
                                        longTime = json.getLong("long");
                                        intercept = json.getString("intercept");
                                    } catch (JSONException e) {
                                        XposedBridge.log(e.toString());
                                    }
                                }
                                if (input.length() > 18) {
                                    duration = longTime;
                                } else {
                                    duration = shortTime;
                                }
                                XposedBridge.log("time:"+shortTime+";"+longTime);
                                if (!shield && !checkIfStartsWith(text, intercept)) {
                                    start(context, text, duration);
                                    initToast(notification);
                                }
                            }
                        }
                    }
                });
    }

    public static boolean checkIfStartsWith(String input, String matchText) {
        if (!input.contains("|")) {
            return matchText.startsWith(input.trim());
        }
        String[] segments = input.split("\\|");
        for (String segment : segments) {
            if (matchText.startsWith(segment.trim())) {
                return true;
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
        icon.setIconFormat("png");
        icon.setIconType(1);

        StatusBarGuideModel.Right right = new StatusBarGuideModel.Right();
        right.setIconParams(icon);
        bar.setLeft(left);
        bar.setRight(right);
        new StrongToastInfo(context, bar, duration);
    }

    private void initToast(Notification notification) {
        XC_MethodHook methodHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                Icon notificationIcon = notification.getLargeIcon();
                if (notificationIcon == null) {
                    notificationIcon = notification.getSmallIcon();
                }
                Bitmap draw = Tools.drawableToBitamp(notificationIcon.loadDrawable(context));
                mLeftTextView = (TextView) XposedHelpers.getObjectField(param.thisObject, "mLeftTextView");
                mRightTextView = (TextView) XposedHelpers.getObjectField(param.thisObject, "mRightTextView");
                mRightImageView = (ImageView) XposedHelpers.getObjectField(param.thisObject, "mRightImageView");
                mRLRight = (RelativeLayout) XposedHelpers.getObjectField(param.thisObject, "mRLRight");
                mLeftTextView.setTextSize(13);
                mRightTextView.setTextSize(13);
                mRightImageView.setImageBitmap(Tools.getCroppedBitmap(draw));
                mRLRight.setOnClickListener(v -> {
                    PendingIntent intent = notification.contentIntent;
                    try {
                        intent.send();
                    } catch (PendingIntent.CanceledException e) {

                    }
                });
                XposedBridge.unhookMethod(param.method, this);  // 移除hook
            }
        };
        XposedHelpers.findAndHookMethod(findClass(Config.ToastPackage), "setValue", methodHook);
    }

}
