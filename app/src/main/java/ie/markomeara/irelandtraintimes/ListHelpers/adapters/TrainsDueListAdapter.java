package ie.markomeara.irelandtraintimes.ListHelpers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import ie.markomeara.irelandtraintimes.ListHelpers.interfaces.TrainListItem;

/**
 * Created by Mark on 16/11/2014.
 */
public class TrainsDueListAdapter extends ArrayAdapter<TrainListItem> {

    private LayoutInflater inflater;

    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }


    public TrainsDueListAdapter(Context context, List<TrainListItem> trains) {
        super(context, 0, trains);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getListViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getListView(inflater, convertView);
    }
}
