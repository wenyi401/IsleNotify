package androidx.top.hyperos.dynamic.notify.ext;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import androidx.top.hyperos.dynamic.notify.R;

public class AppInfo {
    private PackageInfo packageInfo;
    private Context context;
    private String state;
    public AppInfo(Context context, PackageInfo packageInfo, String state) {
        this.context = context;
        this.packageInfo = packageInfo;
        this.state = state;
    }

    public String getAppName() {
        return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
    }

    public String getPackageName() {
        return packageInfo.packageName;
    }

    public Drawable getIcon() {
        return packageInfo.applicationInfo.loadIcon(context.getPackageManager());
    }
    public String getState() {
        return this.state;
    }

    public Boolean getStateBool() {
        return this.state.equals(Tools.getString(R.string.app_modify)) ? true : false;
    }
}
