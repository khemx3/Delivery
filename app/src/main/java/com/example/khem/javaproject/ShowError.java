package com.example.khem.javaproject;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

public class ShowError extends Dialog {

    String msg;

    public ShowError(@NonNull Context context) {
        super(context);
    }

    public ShowError(@NonNull Context context, String msg) {
        super(context);
        this.msg = msg;
    }

    @Override
    public void show() {
        super.show();

        TextView text = (TextView)findViewById(R.id.error_text);
        text.setText(msg);
    }
}
