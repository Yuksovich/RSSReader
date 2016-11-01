package yuriy.rssreader.service;


import android.database.Cursor;
import yuriy.rssreader.data.SingleRSSEntry;

import java.util.ArrayList;

public interface Interactable {

    public ArrayList<SingleRSSEntry> getAllEntriesFromDB();
    public SingleRSSEntry getEntryFromDB(String itemLink);
    public boolean deleteEntryFromDB(String itemLink);
    public boolean deleteAllEntries();
    public boolean deleteAllChannelEntries(String channelTitle);
    public boolean recieveAndPutAllNewEntriesToDB();
    public boolean addNewChannelToList(String url);
    public Cursor getCursor();
}
