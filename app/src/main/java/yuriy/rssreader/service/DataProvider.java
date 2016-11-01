package yuriy.rssreader.service;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ListView;
import yuriy.rssreader.rssexceptions.DatabaseIsEmptyException;
import yuriy.rssreader.rssexceptions.NoRSSContentException;
import yuriy.rssreader.service.realization.DBReader;

final public class DataProvider implements Runnable {

    public final static int STATE_SUCCESS = 0;
    public final static int STATE_FAILURE = 1;
    public final static int STATE_EMPTY = 2;

    private final Context context;
    private final Handler handler;
    private DBReader dbReader = null;

    public DataProvider(final Context context, final Handler handler) {

        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        Looper.prepare();

        try {
            dbReader = new DBReader(context);

            final Cursor cursor = dbReader.getCursor(null);

            final RssListAdapter adapter = new RssListAdapter(context, cursor, false);

            final Message completeMessage = handler.obtainMessage(STATE_SUCCESS, adapter);
            completeMessage.sendToTarget();
        }catch (SQLException e){
            Message completeMessage = handler.obtainMessage(STATE_FAILURE, null);
            completeMessage.sendToTarget();
        }catch (DatabaseIsEmptyException e){
            Message completeMessage = handler.obtainMessage(STATE_EMPTY, null);
            completeMessage.sendToTarget();
        }
        finally {
            if(dbReader!=null) {
                dbReader.close();
            }
        }
        Looper.loop();
    }

}
