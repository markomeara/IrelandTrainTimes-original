package ie.markomeara.irelandtraintimes.views.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.fragments.StationListFragment;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.utils.LocationUtils;

/**
 * Created by markomeara on 03/05/2015.
 */
public class StationRecyclerViewAdapter extends RecyclerView.Adapter<StationRecyclerViewAdapter.ViewHolder>
        implements LocationUtils.LocationListener {

    private static final String TAG = StationRecyclerViewAdapter.class.getSimpleName();
    public List<Station> mAllStations;
    public List<Station> mVisibleStations;
    public StationListFragment.OnStationClickedListener mItemClickListener;

    public StationRecyclerViewAdapter(List<Station> stations,
                                      StationListFragment.OnStationClickedListener itemClickListener){
        super();
        this.mAllStations = stations;
        this.mVisibleStations = new ArrayList<Station>();
        this.mVisibleStations.addAll(mAllStations);
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View createdView = mInflater.inflate(R.layout.list_item_station, parent, false);
        return new ViewHolder(createdView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Station currentStation = mVisibleStations.get(position);
        double stationLatitude = currentStation.getLatitude();
        double stationLongitude = currentStation.getLongitude();
        int distanceBetweenStnAndLocation = LocationUtils.distFromCurrentLocation(stationLatitude, stationLongitude);

        if(distanceBetweenStnAndLocation == LocationUtils.LOCATION_UNKNOWN){
            // Will refresh list items when we have a location to display
            LocationUtils.notifyWhenLocationUpdated(this);
        }
        else {
            holder.setStnDistance(Integer.toString(distanceBetweenStnAndLocation) + "km");
        }

        if(!currentStation.getAlias().isEmpty()){
            holder.setStationDisplayName(currentStation.getAlias());
        }
        else{
            holder.setStationDisplayName(currentStation.getName());
        }

        holder.getView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mItemClickListener.onStationSelected(currentStation);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mVisibleStations.size();
    }

    public void updateDataSet(List<Station> updatedStations){
        boolean updateDisplayed = false;
        if(mAllStations.size() == mVisibleStations.size()){
            // If the list being displayed is not the same as
            // the whole list, then don't update it
            updateDisplayed = true;
        }
        mAllStations = updatedStations;
        if(updateDisplayed){
            mVisibleStations.addAll(mAllStations);
        }
        notifyDataSetChanged();
    }

    public void filter(String filterText) {
        if(filterText.isEmpty() && (mVisibleStations.size() != mAllStations.size())){
            mVisibleStations.clear();
            mVisibleStations.addAll(mAllStations);
            notifyDataSetChanged();
        }
        else if(!filterText.isEmpty()){
            mVisibleStations.clear();
            for(Station stn : mAllStations){
                if (stn.getName().toUpperCase().contains(filterText.toUpperCase())
                        || stn.getAlias().toUpperCase().contains(filterText.toUpperCase())){
                    mVisibleStations.add(stn);
                }
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public void locationUpdated() {
        Log.e(TAG, "Location update notification received");
        notifyDataSetChanged();
        LocationUtils.removeNotificationListener(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View stnItem;

        public ViewHolder(View view) {
            super(view);
            stnItem = view;
        }

        public View getView(){
            return stnItem;
        }

        public void setStationDisplayName(String name){
            TextView stnName = (TextView) stnItem.findViewById(R.id.stationName);
            stnName.setText(name);
        }

        public void setStnDistance(String distance){
            TextView stnDistance = (TextView) stnItem.findViewById(R.id.stationDistance);
            stnDistance.setText(distance);
        }
    }
}
