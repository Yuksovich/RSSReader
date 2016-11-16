package yuriy.rssreader.ui.activity_controllers;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import yuriy.rssreader.MainActivity;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.ChannelSelectionPopup;
import yuriy.rssreader.controllers.RssListAdapter;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.ui.SettingsActivity;
import yuriy.rssreader.ui.dialogs.AddNewUrlDialog;

public final class ToolbarListener implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final static String DIALOG_TAG = "yuriy.rssreader.ui.activity_controllers.ToolbarListener";
    private static final int DIALOG_THEME = 0;
    private static final boolean NOTIFY_IF_NOTHING_NEW = true;
    private static final boolean NO_NOTIFICATION = false;

    private final Activity activity;
    private final ProgressDialog waitingDialog;
    private final ListView listView;

    public ToolbarListener(final Activity activity,
                           final ProgressDialog waitingDialog,
                           final ListView listView) {
        this.activity = activity;
        this.waitingDialog = waitingDialog;
        this.listView = listView;
    }

    @Override
    public void onClick(final View v) {
        if (activity==null){
            return;
        }
        switch (v.getId()) {
            case (R.id.refreshButton_toolbar):
                DatabaseOperationService.refreshDatabase(activity, NOTIFY_IF_NOTHING_NEW, NO_NOTIFICATION);
                DatabaseOperationService.requestEntries(activity, DatabaseOperationService.ALL_CHANNELS);
                waitingDialog.show();
                waitingDialog.setCanceledOnTouchOutside(true);
                waitingDialog.setMessage(activity.getString(R.string.wait_dialog_message));
                break;
            case (R.id.addUrlButton_toolbar):
                DialogFragment dialog = new AddNewUrlDialog();
                dialog.setStyle(DialogFragment.STYLE_NO_TITLE, DIALOG_THEME);
                dialog.show(activity.getFragmentManager(), DIALOG_TAG);
                break;
            case (R.id.filterButton_toolbar):
                showPopupMenu(v);
                break;
            case (R.id.settingsButton_toolbar):
                activity.startActivity(new Intent(activity, SettingsActivity.class));
                break;
            case (R.id.deleteButton_toolbar):
                RssListAdapter adapter = (RssListAdapter) listView.getAdapter();
                if (adapter != null) {
                    if (adapter.isShowDeleteButton()) {
                        adapter.setShowDeleteButton(false);
                        adapter.notifyDataSetChanged();

                    } else {
                        adapter.setShowDeleteButton(true);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void showPopupMenu(final View view) {
        final PopupMenu popupMenu = new PopupMenu(activity, view);
        popupMenu.inflate(R.menu.filter_popup);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {
                if (item.getItemId() == R.id.show_all_popup_item) {
                    MainActivity.setCurrentChannelFilter(DatabaseOperationService.ALL_CHANNELS);
                } else {
                    MainActivity.setCurrentChannelFilter(item.getTitle().toString());
                    MainActivity.resetListPosition();
                }
                DatabaseOperationService.requestEntries(activity, MainActivity.getCurrentChannelFilter());
                return true;
            }
        });

        Menu menu = popupMenu.getMenu();
        ChannelSelectionPopup channelSelectionPopup = new ChannelSelectionPopup(activity, menu);
        channelSelectionPopup.fillMenu();
        popupMenu.show();
    }

    @Override
    public void onRefresh() {
        DatabaseOperationService.refreshDatabase(activity, NOTIFY_IF_NOTHING_NEW, NO_NOTIFICATION);
        DatabaseOperationService.requestEntries(activity, DatabaseOperationService.ALL_CHANNELS);
    }
}
