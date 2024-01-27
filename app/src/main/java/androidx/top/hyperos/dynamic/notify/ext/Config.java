package androidx.top.hyperos.dynamic.notify.ext;

public class Config {
    public static final int MENU_ABOUT = 200;
    public static final int MENU_RESTART = MENU_ABOUT + 1;
    public static final int MENU_HIDE_ICON = MENU_ABOUT + 2;
    public static final int MENU_CONFIG = MENU_ABOUT + 3;
    public static final int MENU_IMPORT = MENU_ABOUT + 4;
    public static final int MENU_EXPORT = MENU_ABOUT + 5;
    public static final int READ_REQUEST_CODE = 100;
    public static final int REQUEST_MANAGE_FILES_ACCESS = READ_REQUEST_CODE + 1;
	public static final String qq = "com.tencent.mobileqq";
    public static final String AppPackage = "androidx.top.hyperos.dynamic.notify";
    public static String SystemUiPackage = "com.android.systemui";
    public static String ToastPath = Tools.concat(SystemUiPackage, ".toast");
    public static String ToastPackage = Tools.concat(ToastPath, ".MIUIStrongToast");
    public static String NotificationListenerPackage = Tools.concat(SystemUiPackage, ".statusbar.notification.MiuiNotificationListener");
    public static String DaggerReferenceGlobalRootComponentPackage = Tools.concat(SystemUiPackage, ".dagger.DaggerReferenceGlobalRootComponent");
}
