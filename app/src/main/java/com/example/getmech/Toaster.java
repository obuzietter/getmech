package com.example.getmech;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
    static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
