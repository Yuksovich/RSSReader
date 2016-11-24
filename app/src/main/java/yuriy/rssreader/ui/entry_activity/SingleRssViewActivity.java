package yuriy.rssreader.ui.entry_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import yuriy.rssreader.R;
import yuriy.rssreader.utils.StateSaver;
import yuriy.rssreader.utils.Theme;

import static yuriy.rssreader.MainActivity.ITEM_LINK;

public final class SingleRssViewActivity extends Activity {
    private String itemLink;
    private boolean notExiting = true;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (Theme.getId() != 0) {
            setTheme(Theme.getId());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_rss_view_activity);

        final Intent intent = getIntent();
        itemLink = intent.getStringExtra(ITEM_LINK);

        final SingleViewFragment viewFragment =
                (SingleViewFragment) getFragmentManager().findFragmentById(R.id.single_view_activity_fragment);
        if (viewFragment != null && itemLink != null) {
            viewFragment.setItemLink(itemLink);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StateSaver.resetLink(this);
        notExiting = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (notExiting && itemLink != null) {
            StateSaver.saveLink(this, itemLink);
        }
    }
}
