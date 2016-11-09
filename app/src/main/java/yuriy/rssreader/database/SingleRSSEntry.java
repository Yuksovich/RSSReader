package yuriy.rssreader.database;


import android.os.Parcel;
import android.os.Parcelable;

public final class SingleRSSEntry implements Parcelable{

    private static final String TRUE_FLAG = "true";
    private static final String EMPTY_STRING = "";

    private final String channelTitle;
    private final String channelImageURL;
    private final String channelDescription;
    private final String itemLink;
    private final String itemTitle;
    private final String itemDescription;
    private final String itemPubDate;
    private String itemBeenViewed;

    private SingleRSSEntry(Builder builder) {

        this.channelTitle = builder.channelTitle;
        this.channelImageURL = builder.channelImageURL;
        this.channelDescription = builder.channelDescription;
        this.itemLink = builder.itemLink;
        this.itemTitle = builder.itemTitle;
        this.itemDescription = builder.itemDescription;
        this.itemPubDate = builder.itemPubDate;
        this.itemBeenViewed = builder.itemBeenViewed;
    }

    public final static class Builder {
        private String channelTitle = EMPTY_STRING;
        private String channelImageURL = EMPTY_STRING;
        private String channelDescription = EMPTY_STRING;
        private String itemLink = EMPTY_STRING;
        private String itemTitle = EMPTY_STRING;
        private String itemDescription = EMPTY_STRING;
        private String itemPubDate = EMPTY_STRING;
        private String itemBeenViewed = EMPTY_STRING;

        public Builder() {

        }

        public Builder channelTitle(final String val) {
            this.channelTitle = val;
            return this;
        }

        public Builder channelImageURL(final String val) {
            this.channelImageURL = val;
            return this;
        }

        public Builder channelDescription(final String val) {
            this.channelDescription = val;
            return this;
        }

        public Builder itemLink(final String val) {
            this.itemLink = val;
            return this;
        }

        public Builder itemTitle(final String val) {
            this.itemTitle = val;
            return this;
        }

        public Builder itemDescription(final String val) {
            this.itemDescription = val;
            return this;
        }

        public Builder itemPubDate(final String val) {
            this.itemPubDate = val;
            return this;
        }

        public Builder itemBeenViewed(final String val) {
            this.itemBeenViewed = val;
            return this;
        }

        public SingleRSSEntry build() {
            return new SingleRSSEntry(this);
        }

    }

    public String getChannelTitle() {
        return channelTitle;
    }

    String getChannelImageURL() {
        return channelImageURL;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public String getItemLink() {
        return itemLink;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemPubDate() {
        return itemPubDate;
    }

    public boolean isBeenViewed() {
        return TRUE_FLAG.equals(itemBeenViewed);
    }

    public void setBeenViewed(){
        itemBeenViewed=TRUE_FLAG;
    }

    private static final int NUMBER_OF_FIELDS = 5;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        final String[] fields = {
                channelTitle,
                itemLink,
                itemTitle,
                itemPubDate,
                itemBeenViewed};
        dest.writeStringArray(fields);
    }

    public static final Parcelable.Creator<SingleRSSEntry> CREATOR = new Parcelable.Creator<SingleRSSEntry>() {
        @Override
        public SingleRSSEntry createFromParcel(Parcel source) {
            final String[] fields = new String[NUMBER_OF_FIELDS];
            source.readStringArray(fields);
            return new SingleRSSEntry.Builder()
                    .channelTitle(fields[0])
                    .itemLink(fields[1])
                    .itemTitle(fields[2])
                    .itemPubDate(fields[3])
                    .itemBeenViewed(fields[4])
                    .build();
        }

        @Override
        public SingleRSSEntry[] newArray(int size) {
            return new SingleRSSEntry[size];
        }
    };
}
