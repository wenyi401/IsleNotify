package androidx.top.hyperos.dynamic.notify.ext;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


import androidx.top.hyperos.dynamic.notify.MainActivity;
import androidx.top.hyperos.dynamic.notify.R;
import androidx.top.hyperos.dynamic.notify.widget.Switch;

import org.json.JSONException;
import org.json.JSONObject;

public class AppInfoDialog {
    private Context context;
    private String packageName;
private SharedPreTool msp;
    public AppInfoDialog(Context context, String packageName) {
        super();
        this.context = context;
        this.packageName = packageName;
    }

    public Dialog initDialog() {
        msp = MainActivity.msp;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_appconfig, null);
        Boolean shield = false;
        long shortTime = 3500; // 默认短时间
        long longTime = 5500; // 默认长时间
        final long shortTimeFinal = shortTime;
        final long longTimeFinal = longTime;
        String intercept = "";

        // 确保关键数据非空
        if (msp.contains(packageName)) {
            String jsonString = msp.optString(packageName, "");
            if (jsonString != null && !jsonString.isEmpty()) {
                try {
                    JSONObject json = new JSONObject(jsonString);
                    shield = json.optBoolean("shield", false);
                    shortTime = json.optInt("short", 3500);
                    longTime = json.optInt("long", 5500);
                    intercept = json.optString("intercept", "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        Dialog dialog = Tools.Alert(context);
        dialog.setContentView(view);
        dialog.setTitle(Tools.getString(R.string.app_dialog_tip));

        // 寻找视图控件
        LinearLayout layout = view.findViewById(R.id.appconfig_layout);
        Switch switchAppShield = view.findViewById(R.id.app_shield);
        EditText editTextAppTimeShort = view.findViewById(R.id.app_time_short);
        EditText editTextAppTimeLong = view.findViewById(R.id.app_time_long);
        EditText editTextAppInfoIntercept = view.findViewById(R.id.app_info_intercept);
        editTextAppInfoIntercept.setBackgroundDrawable(Tools.getRadiusBackground(0xfff6f6f9, 25));
        Button buttonAppCancel = view.findViewById(R.id.app_cancal);
        Button buttonAppSaveConfig = view.findViewById(R.id.app_save_config);
        buttonAppCancel.setBackgroundDrawable(Tools.getRadiusBackground(Tools.getColor(R.color.colorPrimary), 25));
        buttonAppSaveConfig.setBackgroundDrawable(Tools.getRadiusBackground(Tools.getColor(R.color.colorPrimary), 25));

        // 设置视图控件的值
        switchAppShield.setChecked(shield);
        editTextAppTimeShort.setText(String.valueOf(shortTime));
        editTextAppTimeLong.setText(String.valueOf(longTime));
        editTextAppInfoIntercept.setText(intercept);

        // 取消操作
        buttonAppCancel.setOnClickListener(v -> dialog.dismiss());
        // 保存配置操作
        buttonAppSaveConfig.setOnClickListener(v -> {
            try {
                JSONObject json = new JSONObject();
                json.put("shield", switchAppShield.isChecked());
                // 获取并检查短时间输入
                String shortTimeInput = editTextAppTimeShort.getText().toString().trim();
                if (!shortTimeInput.isEmpty()) {
                    json.put("short", Long.valueOf(shortTimeInput));
                }
                else {
                    json.put("short", shortTimeFinal);
                }

                // 获取并检查长时间输入
                String longTimeInput = editTextAppTimeLong.getText().toString().trim();
                if (!longTimeInput.isEmpty()) {
                    json.put("long", Long.valueOf(longTimeInput));
                }
                else {
                    json.put("long", longTimeFinal);
                }
                json.put("intercept", editTextAppInfoIntercept.getText().toString());
                msp.put(packageName, json.toString());
                MainActivity.toast(Tools.getString(R.string.app_save_tip));
            } catch (JSONException | NumberFormatException e) {
                e.printStackTrace();
            }
        });

        return dialog;
    }

}
