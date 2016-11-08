package yuriy.rssreader.utils;


import android.content.Context;

public final class ShortToast {
    private ShortToast(){
        throw new UnsupportedOperationException();
    }

    public static void makeText(final Context context, final String message){
        android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_SHORT).show();
    }

}
