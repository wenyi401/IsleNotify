package androidx.top.hyperos.dynamic.notify;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;

import androidx.top.hyperos.dynamic.notify.ext.AppInfo;
import androidx.top.hyperos.dynamic.notify.ext.AppInfoAdapter;
import androidx.top.hyperos.dynamic.notify.ext.AppInfoDialog;
import androidx.top.hyperos.dynamic.notify.ext.BlurDialog;
import androidx.top.hyperos.dynamic.notify.ext.Config;
import androidx.top.hyperos.dynamic.notify.ext.SharedPreTool;
import androidx.top.hyperos.dynamic.notify.ext.StatusBarGuideModel;
import androidx.top.hyperos.dynamic.notify.ext.StatusBarGuideModel.IconParams;
import androidx.top.hyperos.dynamic.notify.ext.StatusBarGuideModel.Left;
import androidx.top.hyperos.dynamic.notify.ext.StatusBarGuideModel.Right;
import androidx.top.hyperos.dynamic.notify.ext.StatusBarGuideModel.TextParams;
import androidx.top.hyperos.dynamic.notify.ext.Tools;
import androidx.top.hyperos.dynamic.notify.widget.ListView;

import java.util.ArrayList;
import java.util.List;

    public class MainActivity extends BaseActivity {
    public static Context context;
    private ListView infoUser;
    private ListView infoSystem;
    private List<AppInfo> appUser;
    private List<AppInfo> appSystem;
    private AppInfoAdapter adapter;
    private boolean isHide = false;
    private boolean isLog = false;
    public static SharedPreTool msp;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //toast("hello art");
        this.context = this;
        try {
            msp = new SharedPreTool(Tools.getSharedPreferences(context));
            isHide = msp.optBoolean("hide", false);
            isLog = msp.optBoolean("log", false);
            //error("hide" + isHide);
            init();
        } catch (Exception e) {
            error(e.toString());
        }
    }

    private void init() {
        Tools.exec("su", false);
        initTab();
        initBar();
        infoUser = findViewById(R.id.app_list_user);
        infoSystem = findViewById(R.id.app_list_system);
        initAdapter();
        initList(infoUser, appUser);
        initList(infoSystem, appSystem);
        EditText searchText = findViewById(R.id.app_search);
        searchText.setBackground(Tools.getRadiusBackground(Tools.getColor(R.color.pale), 25));
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                List<AppInfo> resultUser = new ArrayList<>();
                List<AppInfo> resultSystem = new ArrayList<>();
                for (AppInfo info : appUser) {
                    if (info.getAppName().contains(text) || info.getPackageName().contains(text)) {
                        resultUser.add(info);
                    }
                }
                for (AppInfo info : appSystem) {
                    if (info.getAppName().contains(text) || info.getPackageName().contains(text)) {
                        resultSystem.add(info);
                    }
                }
                initList(infoUser, resultUser);
                initList(infoSystem, resultSystem);
            }
        });
    }
    private void initTab() {
        TabHost tabhost = findViewById(android.R.id.tabhost);
        findViewById(android.R.id.tabs).setBackground(Tools.getRadiusBackground(Tools.getColor(R.color.pale), 25));
        tabhost.setup();
        TabHost.TabSpec user = tabhost.newTabSpec(Tools.getString(R.string.app_user)).setIndicator(Tools.getString(R.string.app_user)).setContent(R.id.app_list_user);
        TabHost.TabSpec system = tabhost.newTabSpec(Tools.getString(R.string.app_system)).setIndicator(Tools.getString(R.string.app_system)).setContent(R.id.app_list_system);
        tabhost.addTab(user);
        tabhost.addTab(system);
        tabhost.setCurrentTab(0);
    }
    private void initBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.layout_bar);
            View customView = actionBar.getCustomView();
            ImageView refreshButton = customView.findViewById(R.id.app_refresh);
            refreshButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initAdapter();
                    initList(infoUser, appUser);
                    initList(infoSystem, appSystem);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void error(String e) {
            AlertDialog dialog = new BlurDialog(context).setMessage(Tools.concat(Tools.getString(R.string.xposed_error_tip), e)).create();
        dialog.show();
    }

    private void initAdapter() {
        List<PackageInfo> list = getPackageManager().getInstalledPackages(0);
        appUser = new ArrayList<>();
        appSystem = new ArrayList<>();
        for (PackageInfo app : list) {
            String state = msp.contains(app.packageName) ? Tools.getString(R.string.app_modify) : Tools.getString(R.string.app_default);
            if ((app.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appUser.add(new AppInfo(context, app, state));
            } else {
                appSystem.add(new AppInfo(context, app, state));
            }
        }
    }

    private void initList(ListView listView, List<AppInfo> list) {
        adapter = new AppInfoAdapter(context, list);
        adapter.rearrangeItems();
        listView.setAdapter(adapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() * 10);
        listView.setOnItemClickListener((adapterView, view, position, l) -> {
            position = position - listView.getHeaderViewsCount();
            String appPackage = list.get(position).getPackageName();
            if (appPackage != null) {
                showSetDialog(context, appPackage);
            }
        });
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new BlurDialog(context);
            builder.setTitle(Tools.getString(R.string.app_dialog_tip));
            builder.setMessage(Tools.getString(R.string.app_remove_tip));
            builder.setPositiveButton(R.string.app_remove, (dialog, which) -> {
                int in = position - listView.getHeaderViewsCount();
                String appPackage = list.get(in).getPackageName();
                msp.remove(appPackage);
            });
            builder.create().show();
            return true;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case Config.MENU_RESTART:
                Tools.exec("killall com.android.systemui", true);
                return true;
            case Config.MENU_HIDE_ICON:
                boolean hide = !item.isChecked();
                item.setChecked(hide);
                msp.put("hide", hide);
                showLauncherIcon(hide);
                return true;
            case Config.MENU_LOG:
                boolean log = !item.isChecked();
                item.setChecked(log);
                msp.put("log", log);
                return true;
            case Config.MENU_ABOUT:
                AlertDialog dialog = new BlurDialog(context).setMessage(R.string.app_explain).setPositiveButton("加入群聊", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Tools.joinQQGroup(context, Tools.getString(R.string.qq_key));
                    }
                }).create();
                dialog.show();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, Config.MENU_RESTART, 0, Tools.getString(R.string.app_restart));
        /*
        SubMenu configSub = menu.addSubMenu(0, Config.MENU_CONFIG, 0, R.string.app_config);
        configSub.add(0, Config.MENU_IMPORT, 0, R.string.app_config_import);
        configSub.add(0, Config.MENU_EXPORT, 0, R.string.app_config_export);
         */
        menu.add(0, Config.MENU_HIDE_ICON, 0, Tools.getString(R.string.app_hide_icon)).setCheckable(true).setChecked(isHide);
        menu.add(0, Config.MENU_LOG, 0, Tools.getString(R.string.app_log)).setCheckable(true).setChecked(isLog);
        menu.add(0, Config.MENU_ABOUT, 0, Tools.getString(R.string.app_about));
        return super.onCreateOptionsMenu(menu);
    }

    private void showSetDialog(Context context, String packageName) {
        AppInfoDialog dialog = new AppInfoDialog(context, packageName);
        dialog.initDialog().show();
    }

    private void showLauncherIcon(boolean isShow) {
        PackageManager packageManager = this.getPackageManager();
        int show = isShow ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        packageManager.setComponentEnabledSetting(getAliseComponentName(), show, PackageManager.DONT_KILL_APP);
    }

    private ComponentName getAliseComponentName() {
        return new ComponentName(MainActivity.this, BaseActivity.class);
    }

    public static void toast(String str) {
        StatusBarGuideModel bar = new StatusBarGuideModel();
        TextParams text = new TextParams();
        text.setText(str);
        text.setTextColor(-1);

        Left left = new Left();
        left.setTextParams(text);
        IconParams icon = new IconParams();
        icon.setCategory("drawable");
        icon.setIconResName("ic_launcher");
        icon.setIconFormat(StrongToastInfo.format.PNG);
        icon.setIconType(1);

        Right right = new Right();
        right.setIconParams(icon);
        bar.setLeft(left);
        bar.setRight(right);
        new StrongToastInfo(context, bar);
    }
}
