package com.appnd.iosishsearchview;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

public class UiHelper {

    /*public static void hideKeyBoard(Context activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }*/

    /*public static void showKeyboard(Context activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }*/

    public static int DpToPixels(int dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public static void slideIn(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            TranslateAnimation animate = new TranslateAnimation(0, 0, -view.getHeight(), 0);
            animate.setDuration(300);
            view.startAnimation(animate);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void slideToTop(final View view) {
        if (view.getVisibility() != View.GONE) {
            final TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
            animate.setDuration(300);
            view.startAnimation(animate);
            view.setVisibility(View.GONE);
        }
    }
}
