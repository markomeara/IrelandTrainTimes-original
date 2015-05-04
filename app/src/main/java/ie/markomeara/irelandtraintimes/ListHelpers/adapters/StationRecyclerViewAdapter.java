package ie.markomeara.irelandtraintimes.ListHelpers.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.activities.fragments.StationListFragment;
import ie.markomeara.irelandtraintimes.trains.Station;

/**
 * Created by markomeara on 03/05/2015.
 */
public class StationRecyclerViewAdapter extends RecyclerView.Adapter<StationRecyclerViewAdapter.ViewHolder> {

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
        View createdView = mInflater.inflate(R.layout.list_station_item, parent, false);
        return new ViewHolder(createdView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.setStnName(mVisibleStations.get(position).getName());
        // TODO Pass actual calculated distance in here - not the alias
        holder.setStnDistance(mVisibleStations.get(position).getAlias());

        holder.getView().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Station clickedStation = mVisibleStations.get(position);
                        mItemClickListener.onStationSelected(clickedStation);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View stnItem;

        public ViewHolder(View view) {
            super(view);
            stnItem = view;
        }

        public View getView(){
            return stnItem;
        }

        public void setStnName(String name){
            TextView stnName = (TextView) stnItem.findViewById(R.id.stationName);
            stnName.setText(name);
        }

        public void setStnDistance(String distance){
            TextView stnDistance = (TextView) stnItem.findViewById(R.id.stationDistance);
            stnDistance.setText(distance);
        }
    }
}
