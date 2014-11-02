package ie.markomeara.irelandtraintimes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;

/**
 * Created by Mark on 27/09/2014.
 */
public class StationListAdapter extends ArrayAdapter<Station> {

    private static final String TAG = StationListAdapter.class.getSimpleName();

    private final Context context;
    private final List<Station> stations;

    public StationListAdapter(Context context, List<Station> stations) {
        super(context, R.layout.list_stations, stations);
        this.context = context;
        this.stations = stations;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_stations, parent, false);

        TextView stationName = (TextView) rowView.findViewById(R.id.stationName);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);

        if(stations.size() >= position) {
            Station station = stations.get(position);
            stationName.setText(station.getName());
            if (stations.size() > 0) {
                distance.setText(station.getAlias());
            }
        }
        return rowView;
    }
}
