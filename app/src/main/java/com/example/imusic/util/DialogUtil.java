package com.example.imusic.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.imusic.R;

import java.util.Objects;


/**
 * Des：${为DialogFragmnet创建Dialog}
 * Time:2017/8/13 20:38
 * @author Stran
 */
public class DialogUtil {
    public static Dialog getDialogFag(Context context, View view) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AnimationUtil.applyBobbleAnim(view);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow())
              .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow()
              .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow()
              .getAttributes().windowAnimations = R.style.dialogAnim;
        return dialog;

    }


}
