package yuriy.rssreader.controllers.data_input;

import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.rssexceptions.NoRSSContentException;

import java.io.IOException;
import java.util.ArrayList;

public interface Parsable {

    public ArrayList<SingleRSSEntry> receiveAllItems() throws NoRSSContentException, IOException;
    public String getChannelDescription();

}
