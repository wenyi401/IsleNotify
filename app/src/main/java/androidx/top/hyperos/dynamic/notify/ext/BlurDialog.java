package androidx.top.hyperos.dynamic.notify.ext;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.top.hyperos.dynamic.notify.R;

import java.util.function.Consumer;


public class BlurDialog extends AlertDialog.Builder {
    public BlurDialog(Context context) {
        this(context, R.style.BlurDialogTheme);
    }

    public BlurDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public AlertDialog create() {
        AlertDialog dialog = super.create();
        setupWindowBlurListener(dialog);
        return dialog;
    }

    private void setupWindowBlurListener(AlertDialog dialog) {
        Window window = dialog.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
               Consumer<Boolean> windowBlurEnabledListener = enabled -> updateWindowForBlurs(window, enabled);
            window.getDecorView().addOnAttachStateChangeListener(
                    new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View view) {
                            window.getWindowManager().addCrossWindowBlurEnabledListener(
                                    windowBlurEnabledListener);
                        }

                        @Override
                        public void onViewDetachedFromWindow(View view) {
                            window.getWindowManager().removeCrossWindowBlurEnabledListener(
                                    windowBlurEnabledListener);
                        }
                    });
        }
    }

    private void updateWindowForBlurs(Window window, boolean blursEnabled) {
        float mDimAmountWithBlur = 0.1f;
        float mDimAmountNoBlur = 0.35f;
        window.setDimAmount(blursEnabled ? mDimAmountWithBlur : mDimAmountNoBlur);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.getAttributes().setBlurBehindRadius(20);
            window.setAttributes(window.getAttributes());
        }
    }

}
