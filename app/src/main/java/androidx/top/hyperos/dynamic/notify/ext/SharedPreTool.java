package androidx.top.hyperos.dynamic.notify.ext;

import android.content.SharedPreferences;

import java.util.Set;

public class SharedPreTool {
    private SharedPreferences msp;
    private SharedPreferences.Editor mspEdit;
    private String M_SHARED = "msp";

    public SharedPreTool(SharedPreferences msp) {
        this.msp = msp;
        this.mspEdit = this.msp.edit();
    }

    public void put(String key, Object value) {
        if (value instanceof Integer) {
            mspEdit.putInt(key, (int) value);
        }
        if (value instanceof Boolean) {
            mspEdit.putBoolean(key, (Boolean) value);
        }
        if (value instanceof Float) {
            mspEdit.putFloat(key, (Float) value);
        }
        if (value instanceof Long) {
            mspEdit.putLong(key, (Long) value);
        }
        if (value instanceof Set) {
            mspEdit.putStringSet(key, (Set<String>) value);
        }
        if (value instanceof String) {
            mspEdit.putString(key, (String) value);
        }
        mspEdit.apply();
    }

    public String optString(String key, String defValue) {
        return msp.getString(key, defValue);
    }

    public int optInt(String key, int defValue) {
        return msp.getInt(key, defValue);
    }

    public Boolean optBoolean(String key, Boolean defValue) {
        return msp.getBoolean(key, defValue);
    }

    public Long optLong(String key, Long defValue) {
        return msp.getLong(key, defValue);
    }

    public Float optFloat(String key, float defValue) {
        return msp.getFloat(key, defValue);
    }

    public Set<String> optStringSet(String key, Set<String> defValue) {
        return msp.getStringSet(key, defValue);
    }

    public Boolean contains(String key) {
        return msp.contains(key);
    }

    public String getType() {
        return M_SHARED;
    }

    public void remove(String key) {
        mspEdit.remove(key);
        mspEdit.apply();
    }
}
