package ie.markomeara.irelandtraintimes.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Station;

/**
 * Created by Mark on 27/09/2014.
 */
public class StationListAdapter extends ArrayAdapter<Station> implements Filterable {

    private static final String TAG = StationListAdapter.class.getSimpleName();

    private final Context context;
    private List<Station> allStations;
    private List<Station> filteredStations;
    private Filter stationFilter;

    public StationListAdapter(Context context, List<Station> stations) {
        super(context, R.layout.list_stations, stations);
        this.context = context;
        this.allStations = stations;

        this.filteredStations = this.allStations;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View rowView = convertView;

        if(rowView == null){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_stations, parent, false);
        }

        TextView stationName = (TextView) rowView.findViewById(R.id.stationName);
        TextView distance = (TextView) rowView.findViewById(R.id.distance);

        Station station = filteredStations.get(position);
        stationName.setText(station.getName());
        if (filteredStations.size() > 0) {
            distance.setText(station.getAlias());
        }

        return rowView;
    }

    @Override
    public Filter getFilter(){
        if(stationFilter == null){
            stationFilter = new StationListFilter();
        }
        return stationFilter;
    }

    @Override
    public int getCount(){
        return this.filteredStations.size();
    }

    private class StationListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence filterTerm) {

            FilterResults results = new FilterResults();

            if (filterTerm == null || filterTerm.length() == 0) {
                // No filter implemented we return all the list
                results.values = allStations;
                results.count = allStations.size();
            }
            else {
                // We perform filtering operation
                List<Station> reducedStations = new ArrayList<Station>();

                for (Station stn : allStations) {
                    if (stn.getName().toUpperCase().contains(filterTerm.toString().toUpperCase())
                            || stn.getAlias().contains(filterTerm.toString().toUpperCase()))
                        reducedStations.add(stn);
                }
                // TODO Sort stations so that those that start with search term appear first

                results.values = reducedStations;
                results.count = reducedStations.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                filteredStations = (List<Station>) results.values;
                notifyDataSetChanged();
            }
         }
    }

}
