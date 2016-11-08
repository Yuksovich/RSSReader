/*
package yuriy.rssreader.controllers;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import yuriy.rssreader.rssexceptions.DatabaseIsEmptyException;
import yuriy.rssreader.database.DBReader;

final public class ListViewDataProvider implements Runnable {

    public final static int STATE_SUCCESS = 0;
    public final static int STATE_FAILURE = 1;
    public final static int STATE_EMPTY = 2;

    public final static String []CURSOR_ALL_ENRTRIES = null;

    private final Context context;
    private final Handler handler;
    private DBReader dbReader = null;

    public ListViewDataProvider(final Context context, final Handler handler) {

        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        Looper.prepare();

        try {
            dbReader = new DBReader(context);

            final Cursor cursor = dbReader.getCursor(CURSOR_ALL_ENRTRIES);

            final RssListAdapter adapter = new RssListAdapter(context, cursor, false);

            final Message completeMessage = handler.obtainMessage(STATE_SUCCESS, adapter);
            completeMessage.sendToTarget();
        } catch (SQLException e) {
            Message completeMessage = handler.obtainMessage(STATE_FAILURE, null);
            completeMessage.sendToTarget();
        } catch (DatabaseIsEmptyException e) {
            Message completeMessage = handler.obtainMessage(STATE_EMPTY, null);
            completeMessage.sendToTarget();
        } finally {
            if (dbReader != null) {
                dbReader.close();
            }
        }
        Looper.loop();
    }

}
*/
