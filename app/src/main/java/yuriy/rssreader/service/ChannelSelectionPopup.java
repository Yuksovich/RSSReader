package yuriy.rssreader.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;

import java.util.Map;
import java.util.Set;

public final class ChannelSelectionPopup {
    private final static String CHANNELS = "channels";

    private final Context context;
    private final Menu menu;
    private final SharedPreferences sharedPreferences;
    public ChannelSelectionPopup(final Context context, final Menu menu) {
        this.context = context;
        this.menu = menu;
        sharedPreferences = context.getSharedPreferences(CHANNELS, Context.MODE_PRIVATE);
    }

    public Menu getMenu(){
        final Map<String, ?>map = sharedPreferences.getAll();
        final Set<?> channelsSet = map.entrySet();
        for(Object channel:channelsSet){
            menu.add((String)channel);
        }
        return menu;
    }


}
