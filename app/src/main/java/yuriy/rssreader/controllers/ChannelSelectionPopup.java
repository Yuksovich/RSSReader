package yuriy.rssreader.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import yuriy.rssreader.R;

import java.util.Map;
import java.util.Set;

public final class ChannelSelectionPopup {
    private final static String CHANNELS = "channels";
    private final Menu menu;
    private final SharedPreferences sharedPreferences;
    public ChannelSelectionPopup(final Context context, final Menu menu) {
          this.menu = menu;
        sharedPreferences = context.getSharedPreferences(CHANNELS, Context.MODE_PRIVATE);
    }

    public Menu getMenu(){
        final Map<String, ?>channelsMap = sharedPreferences.getAll();
        final Set<String>channelUrlsSet = channelsMap.keySet();
        for(String channelUrl:channelUrlsSet){
            menu.add(R.id.channels_list_filter, Menu.NONE, Menu.NONE, sharedPreferences.getString(channelUrl, channelUrl));
        }

        return menu;
    }


}
