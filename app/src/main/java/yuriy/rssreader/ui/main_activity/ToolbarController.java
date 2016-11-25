package yuriy.rssreader.ui.main_activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;
import yuriy.rssreader.R;
import yuriy.rssreader.controllers.ChannelSelectionPopup;
import yuriy.rssreader.controllers.RssEntriesListAdapter;
import yuriy.rssreader.services.DatabaseOperationService;
import yuriy.rssreader.ui.settings_activity.SettingsActivity;
import yuriy.rssreader.utils.StateSaver;

public final class ToolbarController
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private final static String DIALOG_TAG = "yuriy.rssreader.ui.main_activity.ToolbarController";
    private static final int DIALOG_THEME = 0;
    private static final boolean NOTIFY_IF_NOTHING_NEW = true;
    private static final boolean NO_NOTIFICATION = false;

    private final Activity activity;
    private final ListView listView;

    public ToolbarController(final Activity activity,
                             final ListView listView) {
        this.activity = activity;
        this.listView = listView;
    }

    @Override
    public void onClick(final View v) {
        if (activity == null || v == null || listView == null) {
            return;
        }
        switch (v.getId()) {
            case (R.id.refreshButton_toolbar):
                DatabaseOperationService.refreshDatabase(activity, NOTIFY_IF_NOTHING_NEW, NO_NOTIFICATION);
                DatabaseOperationService.requestEntries(activity, DatabaseOperationService.ALL_CHANNELS);
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
                RssEntriesListAdapter adapter = (RssEntriesListAdapter) listView.getAdapter();
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
                    StateSaver.saveChannelFilter(activity, DatabaseOperationService.ALL_CHANNELS);

                } else {
                    StateSaver.saveChannelFilter(activity, item.getTitle().toString());
                    StateSaver.resetListPosition(activity);
                }
                final String currentChannelFilter = StateSaver.getChannelFilter(activity);
                DatabaseOperationService.requestEntries(activity, currentChannelFilter);
                return true;
            }
        });

        final Menu menu = popupMenu.getMenu();
        ChannelSelectionPopup.fillMenuWithChannelsFromSPrefs(activity, menu);
        popupMenu.show();
    }

    @Override
    public void onRefresh() {
        if (activity == null) {
            return;
        }
        DatabaseOperationService.refreshDatabase(activity, NOTIFY_IF_NOTHING_NEW, NO_NOTIFICATION);
        DatabaseOperationService.requestEntries(activity, DatabaseOperationService.ALL_CHANNELS);
    }
}
