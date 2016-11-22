package yuriy.rssreader.ui.main_activity;


import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import yuriy.rssreader.R;

public final class EntriesListFragment extends ListFragment {
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, container);
    }
}
