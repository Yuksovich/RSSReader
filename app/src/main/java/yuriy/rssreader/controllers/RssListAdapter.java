package yuriy.rssreader.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import yuriy.rssreader.R;
import yuriy.rssreader.database.SingleRSSEntry;
import yuriy.rssreader.services.SingleEntryOperationService;

import java.util.ArrayList;


public final class RssListAdapter extends ArrayAdapter<SingleRSSEntry> {
    private boolean showDeleteButton = false;

    public RssListAdapter(final Context context, ArrayList<SingleRSSEntry> entries) {
        super(context, R.layout.entry_in_list_not_seen, entries);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final SingleRSSEntry entry = getItem(position);
        ViewHolder viewHolder;


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.entry_in_list_not_seen, parent, false);
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
                }
            });
        } else {
            viewHolder.deleteItem.setVisibility(View.INVISIBLE);
        }

        if (entry.isBeenViewed()) {
            viewHolder.itemTitle.setTextColor(getContext().getResources().getColor(R.color.item_text_was_read_listView_entry));
            viewHolder.itemTitle.setTextColor(getContext().getResources().getColor(R.color.item_text_was_read_listView_entry));
            viewHolder.itemPubDate.setTextColor(getContext().getResources().getColor(R.color.item_text_was_read_listView_entry));
            viewHolder.channelTitle.setTextColor(getContext().getResources().getColor(R.color.item_text_was_read_listView_entry));
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.item_background_was_read_listView_entry));
        } else {
            viewHolder.itemTitle.setTextColor(getContext().getResources().getColor(R.color.item_text_not_read_listView_entry));
            viewHolder.itemTitle.setTextColor(getContext().getResources().getColor(R.color.item_text_not_read_listView_entry));
            viewHolder.itemPubDate.setTextColor(getContext().getResources().getColor(R.color.item_text_not_read_listView_entry));
            viewHolder.channelTitle.setTextColor(getContext().getResources().getColor(R.color.item_text_not_read_listView_entry));
            convertView.setBackgroundColor(getContext().getResources().getColor(R.color.item_background_not_read_listView_entry));
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView itemTitle;
        TextView itemPubDate;
        TextView channelTitle;
        TextView itemLink;
        ImageButton deleteItem;
    }


    public void setShowDeleteButton(boolean showDeleteButton) {
        this.showDeleteButton = showDeleteButton;
    }

    public boolean isShowDeleteButton() {
        return showDeleteButton;
    }

}
