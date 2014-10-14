package ie.markomeara.irelandrailtimes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ie.markomeara.irelandrailtimes.R;

/**
 * Created by Mark on 27/09/2014.
 */
public class FavListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> stations;

    public FavListAdapter(Context context, List<String[]> items) {
        super(context, R.layout.favlist_search, items);
        this.context = context;
        stations = items;
    }

    public FavListAdapter(Context context, int resource, List<String[]> items) {
        super(context, resource, items);
        this.context = context;
        stations = items;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.favlist_search, parent, false);

        TextView stationName = (TextView) rowView.findViewById(R.id.favStationName);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);

        // TODO Array bounds / null checking
        String[] station = stations.get(position);
        stationName.setText(station[0]);
        distance.setText(station[1]);
        return rowView;
    }
}
