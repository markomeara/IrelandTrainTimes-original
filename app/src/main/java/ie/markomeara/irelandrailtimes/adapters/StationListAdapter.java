package ie.markomeara.irelandrailtimes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ie.markomeara.irelandrailtimes.R;
import ie.markomeara.irelandrailtimes.Station;

/**
 * Created by Mark on 05/10/2014.
 */
public class StationListAdapter extends ArrayAdapter<Station> {

    private final Context context;
    private final List<Station> stations;

    public StationListAdapter(Context context, List<Station> items) {
        super(context, R.layout.favlist_search, items);
        this.context = context;
        stations = items;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.favlist_search, parent, false);

        if(stations.get(position).isFavourite()){
            rowView.setBackgroundResource(R.drawable.favsearchitem_bg);
            // TODO distance
        }

        TextView stationName = (TextView) rowView.findViewById(R.id.favStationName);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);

        // TODO Array bounds / null checking
    //    String[] station = stations.get(position);
        stationName.setText(station[0]);
        distance.setText(station[1]);
        return rowView;
    }


}
