package androidx.top.hyperos.dynamic.notify.ext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.top.hyperos.dynamic.notify.MainActivity;
import androidx.top.hyperos.dynamic.notify.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Tools {
    public static final String DEFAULT_STORAGE = "config_preferences";

    public static String concat(CharSequence... charSequenceArr) {
        try {
            return TextUtils.concat(charSequenceArr).toString();
        } catch (Throwable th) {
            StringBuilder sb = new StringBuilder();
            for (CharSequence charSequence : charSequenceArr) {
                sb.append(charSequence);
            }
            return sb.toString();
        }
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        Bitmap bitmap = null;
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        int halfWidth = bitmap.getWidth() / 2;
        int halfHeight = bitmap.getHeight() / 2;
        canvas.drawCircle(halfWidth, halfHeight, Math.max(halfWidth, halfHeight), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    public static String toJson(Object obj) {
        JSONObject jsonObject = new JSONObject(getFieldMap(obj));
        return jsonObject.toString();
    }

    private static Map<String, Object> getFieldMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    if (value.getClass().getName().startsWith("java")) {
                        map.put(field.getName(), value);
                    } else {
                        map.put(field.getName(), getFieldMap(value));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    @SuppressLint("WorldReadableFiles")
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.createDeviceProtectedStorageContext().getSharedPreferences(DEFAULT_STORAGE, Context.MODE_WORLD_READABLE);
    }
    public static void exec(String command, Boolean isSu) {
        try {
            if (isSu) {
                Process p = Runtime.getRuntime().exec("su");
                OutputStream outputStream = p.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeBytes(command);
                dataOutputStream.flush();
                dataOutputStream.close();
                return;
            } else {
                Runtime.getRuntime().exec(command);
            }
        } catch (Throwable e) {
        }
    }
    public static Context getContext() {
        Context context = MainActivity.context;
        return context;
    }
    public static String getString(int i) {
        String str = null;
        try {
            Context context = getContext();
            str = Build.VERSION.SDK_INT >= 23 ? context.getString(i) : context.getResources().getString(i);
        } catch (Throwable th) {
        }
        return str;
    }

    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(
                "mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D"
                        + key));
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    public static int getColor(int i) {
        return getColor(i, -1);
    }

    public static int getColor(int colorId, int colorReturn) {
        try {
            Context context = getContext();
            colorReturn = Build.VERSION.SDK_INT >= 23 ? context.getColor(colorId) : context.getResources().getColor(colorId);
        } catch (Throwable th) {

        }
        return colorReturn;
    }

    public static GradientDrawable getRadiusBackground(int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    public static Dialog Alert(Context context) {
        Dialog dialog = new Dialog(context);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setBackgroundDrawable(Tools.getRadiusBackground(Tools.getColor(R.color.white), 45));
        return dialog;
    }
}
