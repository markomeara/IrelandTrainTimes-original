package ie.markomeara.irelandtraintimes.ListHelpers;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ie.markomeara.irelandtraintimes.ListHelpers.adapters.TrainsDueListAdapter;
import ie.markomeara.irelandtraintimes.ListHelpers.interfaces.TrainListItem;
import ie.markomeara.irelandtraintimes.R;

/**
 * Created by Mark on 16/11/2014.
 */
public class TrainListHeader implements TrainListItem {
    @Override
    public int getListViewType() {
        return TrainsDueListAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getListView(LayoutInflater inflater, View convertView) {
        View headerView;
        if(convertView == null){
            headerView = (View) inflater.inflate(R.layout.list_trains_header, null);
        }
        else{
            headerView = convertView;
        }

        TextView header = (TextView) headerView.findViewById(R.id.trainsDueListHeader);
        header.setText("Header placeholder text");
        return headerView;
    }
}
