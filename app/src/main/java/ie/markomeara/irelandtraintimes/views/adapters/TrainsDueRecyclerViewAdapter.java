package ie.markomeara.irelandtraintimes.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.fragments.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.model.Train;
import ie.markomeara.irelandtraintimes.model.TrainListHeader;
import ie.markomeara.irelandtraintimes.model.TrainListItem;
import ie.markomeara.irelandtraintimes.viewholder.TrainViewHolder;

/**
 * Created by markomeara on 08/11/2015.
 */
public class TrainsDueRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TrainListItem> mListItems;
    private StationNextTrainsFragment mParentFragment;

    public TrainsDueRecyclerViewAdapter(List<TrainListItem> trains, StationNextTrainsFragment parentFragment) {
        this.mListItems = trains;
        this.mParentFragment = parentFragment;
    }

    public enum RowType {
        TRAIN, HEADER
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        if(viewType == RowType.HEADER.ordinal()){
            View createdView = mInflater.inflate(R.layout.list_header_trains, parent, false);
            return new TrainDirectionViewHolder(createdView);
        }
        else{
            View createdView = mInflater.inflate(R.layout.list_item_trains, parent, false);
            return new TrainViewHolder(createdView, mParentFragment);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TrainListItem listItem = mListItems.get(position);
        if(listItem.getViewType() == RowType.HEADER.ordinal()){
            TrainDirectionViewHolder directionViewHolder = (TrainDirectionViewHolder) holder;
            TrainListHeader directionHeading = (TrainListHeader) listItem;
            populateHeaderItem(directionViewHolder, directionHeading);
        }
        else{
            TrainViewHolder trainViewHolder = (TrainViewHolder) holder;
            Train train = (Train) listItem;
            populateTrainItem(trainViewHolder, train);
        }
    }

    public Train getTrainAtPosition(int pos){
        TrainListItem listItem = mListItems.get(pos);
        Train train = null;
        if(listItem instanceof Train){
            train = (Train) listItem;
        }
        return train;
    }

    private void populateHeaderItem(TrainDirectionViewHolder directionViewHolder, TrainListHeader header){
        directionViewHolder.setDirection(header.getHeadingText());
    }

    private void populateTrainItem(TrainViewHolder trainViewHolder, Train train) {
        trainViewHolder.setTrainDestination(train.getDestination());
        trainViewHolder.setTrainDueMins(train.getDueIn());
        trainViewHolder.setTrainDueTime(train.getExpDepart());
        trainViewHolder.setTrainDelayMins(train.getDelayMins());
    }


    @Override
    public int getItemCount() {
        return mListItems.size();
    }

    public static class TrainDirectionViewHolder extends RecyclerView.ViewHolder {

        private final View trainDirectionHeading;
        @Bind(R.id.trainsDueListHeader)
        TextView directionTV;

        public TrainDirectionViewHolder(View view){
            super(view);
            trainDirectionHeading = view;
            ButterKnife.bind(this, view);
        }

        public void setDirection(String direction){
            directionTV.setText(direction);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mListItems.get(position).getViewType();
    }

}
