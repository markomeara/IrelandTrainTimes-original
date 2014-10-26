package ie.markomeara.irelandtraintimes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ie.markomeara.irelandtraintimes.R;

/**
 * Created by Mark on 27/09/2014.
 */
public class StationListAdapter extends ArrayAdapter<String[]> {

    private final Context context;
    private final List<String[]> stations;

    public StationListAdapter(Context context, List<String[]> items) {
        super(context, R.layout.stationlist_search, items);
        this.context = context;
        stations = items;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.stationlist_search, parent, false);

        TextView stationName = (TextView) rowView.findViewById(R.id.stationName);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);

        if(stations.size() >= position) {
            String[] station = stations.get(position);
            stationName.setText(station[0]);
            if (station.length > 0) {
                distance.setText(station[1]);
            }
        }
        return rowView;
    }
}
