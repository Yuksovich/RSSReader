package yuriy.rssreader.ui.settings_activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import yuriy.rssreader.MainActivity;
import yuriy.rssreader.R;
import yuriy.rssreader.services.DatabaseOperationService;

public final class ConfirmDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.confirm_dialog, container);


        final Button yesButton = (Button) view.findViewById(R.id.yes_button_confirm_dialog);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DatabaseOperationService.deleteChannelsEntries(v.getContext(), DatabaseOperationService.ALL_CHANNELS);
                startActivity(new Intent(v.getContext(), MainActivity.class));
                dismiss();
            }
        });

        final Button noButton = (Button) view.findViewById(R.id.no_button_confirm_dialog);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        return view;
    }
}
