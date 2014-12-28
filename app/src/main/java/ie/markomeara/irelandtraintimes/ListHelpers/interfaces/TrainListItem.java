package ie.markomeara.irelandtraintimes.ListHelpers.interfaces;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Mark on 16/11/2014.
 */
public interface TrainListItem {
    public int getListViewType();
    public View getListView(LayoutInflater inflater, View convertView);
}
