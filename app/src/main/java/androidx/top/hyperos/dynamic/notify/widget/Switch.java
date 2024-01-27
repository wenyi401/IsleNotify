package androidx.top.hyperos.dynamic.notify.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.top.hyperos.dynamic.notify.R;
import androidx.top.hyperos.dynamic.notify.ext.Tools;

public class Switch extends android.widget.Switch {
    public Switch(Context context) {
        super(context);
        init();
    }

    public Switch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Switch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBackgroundDrawable(Tools.getRadiusBackground(Tools.getColor(R.color.colorPrimary), 45));
        setTrackDrawable(Tools.getRadiusBackground(Tools.getColor(R.color.colorPrimary), 45));
        setText("");
    }
}
