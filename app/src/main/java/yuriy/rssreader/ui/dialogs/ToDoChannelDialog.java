package yuriy.rssreader.ui.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import yuriy.rssreader.R;
import yuriy.rssreader.services.DatabaseOperationService;

import static yuriy.rssreader.services.DatabaseOperationService.CHANNELS;

public final class ToDoChannelDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.todo_channel_dialog, container);
        final TextView textView = (TextView) view.findViewById(R.id.channel_todo_dialog);
        final String channelUrl = getTag();
        textView.setText(channelUrl);

        final Button deleteChannel = (Button) view.findViewById(R.id.remove_channel_button_todo_dialog);
        deleteChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                deleteChannel(v.getContext(), channelUrl);
                dismiss();
            }
        });

        final Button deleteEntries = (Button) view.findViewById(R.id.remove_entries_button_todo_dialog);
        deleteEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DatabaseOperationService.deleteChannelsEntries(v.getContext(), channelUrl);
                dismiss();
            }
        });

        final Button deleteChannelAndEntries = (Button) view.findViewById(R.id.remove_channel_and_entries_button_todo_dialog);
        deleteChannelAndEntries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DatabaseOperationService.deleteChannelsEntries(v.getContext(), channelUrl);
                deleteChannel(v.getContext(), channelUrl);
                dismiss();
            }
        });

        final Button cancel = (Button) view.findViewById(R.id.cancel_button_todo_dialog);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });
        return view;
    }

    private void deleteChannel(final Context context, final String channel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(channel);
        editor.apply();
    }
}
