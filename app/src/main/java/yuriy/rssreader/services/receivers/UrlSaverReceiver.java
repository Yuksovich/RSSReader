package yuriy.rssreader.services.receivers;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import yuriy.rssreader.services.UrlSaverService;

public final class UrlSaverReceiver extends BroadcastReceiver {
    private final DialogFragment dialog;

    public UrlSaverReceiver(final DialogFragment dialog) {
        this.dialog = dialog;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        final String action = intent.getAction();
        final String message = intent.getStringExtra(action);
        if(message == null){
            return;
        }
        switch (action) {
            case (UrlSaverService.FAIL):
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                break;
            case (UrlSaverService.SUCCESS):
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                break;
            default:
                break;
        }
    }
}
