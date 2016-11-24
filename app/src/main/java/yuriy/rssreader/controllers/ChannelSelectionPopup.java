package yuriy.rssreader.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import yuriy.rssreader.R;

import java.util.Map;
import java.util.Set;

import static yuriy.rssreader.services.DatabaseOperationService.CHANNELS;

public final class ChannelSelectionPopup {

    private final Menu menu;
    private final SharedPreferences sharedPreferences;

    private ChannelSelectionPopup(final Context context, final Menu menu) {
        this.menu = menu;
        sharedPreferences = context.getSharedPreferences(CHANNELS, Context.MODE_PRIVATE);
    }

    private void fillMenu() {
        final Map<String, ?> channelsMap = sharedPreferences.getAll();
        final Set<String> channelUrlsSet = channelsMap.keySet();
        for (String channelUrl : channelUrlsSet) {
            menu.add(R.id.channels_list_filter, Menu.NONE, Menu.NONE, sharedPreferences.getString(channelUrl, channelUrl));
        }

    }

    public static void fillMenuWithChannelsFromSPrefs(final Context context, final Menu menu) {
        if (context == null || menu == null) {
            return;
        }
        final ChannelSelectionPopup popup = new ChannelSelectionPopup(context, menu);
        popup.fillMenu();

    }


}
