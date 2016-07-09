package ie.markomeara.irelandtraintimes.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.ui.fragment.StationListFragment;
import ie.markomeara.irelandtraintimes.model.Station;
import ie.markomeara.irelandtraintimes.utils.LocationUtils;
import ie.markomeara.irelandtraintimes.viewholder.StationViewHolder;

public class StationRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements LocationUtils.LocationListener {

    private static final String TAG = StationRecyclerViewAdapter.class.getSimpleName();
    private List<Station> mAllStations;
    private List<Station> mVisibleStations;
    private StationListFragment.OnStationClickedListener mItemClickListener;

    public StationRecyclerViewAdapter(List<Station> stations,
                                      StationListFragment.OnStationClickedListener itemClickListener){
        super();
        this.mAllStations = stations;
        this.mVisibleStations = new ArrayList<>();
        this.mVisibleStations.addAll(mAllStations);
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        View createdView = mInflater.inflate(R.layout.list_item_station, parent, false);
        return new StationViewHolder(createdView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        StationViewHolder stationViewHolder = (StationViewHolder) holder;

        final Station currentStation = mVisibleStations.get(position);
        double stationLatitude = currentStation.getLatitude();
        double stationLongitude = currentStation.getLongitude();
        int distanceBetweenStnAndLocation = LocationUtils.distFromCurrentLocation(stationLatitude, stationLongitude);

        if(distanceBetweenStnAndLocation == LocationUtils.LOCATION_UNKNOWN){
            // Will refresh list items when we have a location to display
            LocationUtils.notifyWhenLocationUpdated(this);
        }
        else {
            stationViewHolder.setStnDistance(Integer.toString(distanceBetweenStnAndLocation) + "km");
        }

        stationViewHolder.setStationDisplayName(currentStation.getDisplayName());

        stationViewHolder.getView().setOnClickListener(
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
                        || (stn.getAlias() != null && stn.getAlias().toUpperCase().contains(filterText.toUpperCase()))){
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

}
