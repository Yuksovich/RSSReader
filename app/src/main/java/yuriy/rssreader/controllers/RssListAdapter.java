package yuriy.rssreader.controllers;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import yuriy.rssreader.MainActivity;
import yuriy.rssreader.R;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.services.SingleEntryOperationService;
import yuriy.rssreader.utils.Theme;

import java.util.ArrayList;


public final class RssListAdapter extends ArrayAdapter<SingleRSSEntry> {
    private boolean showDeleteButton = false;

    public RssListAdapter(final Context context, ArrayList<SingleRSSEntry> entries) {
        super(context, R.layout.entry_in_list, entries);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final SingleRSSEntry entry = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.entry_in_list, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.itemTitle = (TextView) convertView.findViewById(R.id.item_title_listView_entry);
            viewHolder.itemPubDate = (TextView) convertView.findViewById(R.id.item_pubDate_listView_entry);
            viewHolder.channelTitle = (TextView) convertView.findViewById(R.id.channel_title__listView_entry);
            viewHolder.itemLink = (TextView) convertView.findViewById(R.id.item_link_listView_entry);
            viewHolder.deleteItem = (ImageButton) convertView.findViewById(R.id.delete_button_listView_entry);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemTitle.setText(entry.getItemTitle());
        viewHolder.itemPubDate.setText(entry.getItemPubDate());
        viewHolder.channelTitle.setText(entry.getChannelTitle());
        viewHolder.itemLink.setText(entry.getItemLink());

        if (showDeleteButton) {
            viewHolder.deleteItem.setVisibility(View.VISIBLE);
            viewHolder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    remove(entry);
                    SingleEntryOperationService.deleteEntry(getContext(), entry.getItemLink());
                    if(isEmpty()){
                       getContext().startActivity(new Intent(getContext(), MainActivity.class));
                    }
                }
            });
        } else {
            viewHolder.deleteItem.setVisibility(View.INVISIBLE);
        }

        if (entry.isBeenViewed()) {
            viewHolder.itemTitle.setTextColor(Theme.getTextColorSeen(getContext()));
            viewHolder.itemPubDate.setTextColor(Theme.getTextColorSeen(getContext()));
            viewHolder.channelTitle.setTextColor(Theme.getTextColorSeen(getContext()));
            viewHolder.channelTitle.setTextColor(Theme.getTextColorSeen(getContext()));

        } else {
            viewHolder.itemTitle.setTextColor(Theme.getTextColor(getContext()));
            viewHolder.itemPubDate.setTextColor(Theme.getTextColor(getContext()));
            viewHolder.channelTitle.setTextColor(Theme.getTextColor(getContext()));
            viewHolder.channelTitle.setTextColor(Theme.getTextColor(getContext()));

        }

        convertView.setHapticFeedbackEnabled(true);
        return convertView;
    }

    private static class ViewHolder {
        TextView itemTitle;
        TextView itemPubDate;
        TextView channelTitle;
        TextView itemLink;
        ImageButton deleteItem;
    }

    public void setShowDeleteButton(final boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
    }

    public boolean isShowDeleteButton() {
        return showDeleteButton;
    }


}
