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

/**
 * Created by markomeara on 08/11/2015.
 */
public class TrainsDueRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<TrainListItem> mListItems;
    private StationNextTrainsFragment mParentFragment;
    private static final int NO_COLOR = -1;

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

    public static class TrainViewHolder extends RecyclerView.ViewHolder {

        private final View mTrainItem;
        @Bind(R.id.reminderButton)
        View mReminderBtn;
        @Bind(R.id.traindue_dest_TV)
        TextView trainDest_TV;
        @Bind(R.id.traindue_mins_TV)
        TextView trainDueMins_TV;
        @Bind(R.id.traindue_delay_TV)
        TextView trainDelayMins_TV;
        @Bind(R.id.traindue_time_TV)
        TextView trainDueTime_TV;

        private final StationNextTrainsFragment mListener;

        public TrainViewHolder(View view, StationNextTrainsFragment listener) {
            super(view);
            mTrainItem = view;
            ButterKnife.bind(this, view);
            mListener = listener;
            createOnClickListeners();
        }

        public View getView(){
            return mTrainItem;
        }

        private void createOnClickListeners(){
            mTrainItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    if(adapterPosition != RecyclerView.NO_POSITION){
                        mListener.onTrainSelected(getAdapterPosition());
                    }
                }
            });

            mReminderBtn.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        // TODO Something
                    }
                }
            }));
        }
        public void setTrainDestination(String destination){
            trainDest_TV.setText(destination);
        }

        public void setTrainDueMins(int trainDueMins){
            trainDueMins_TV.setText(Integer.toString(trainDueMins) + " mins");
        }

        public void setTrainDelayMins(int trainDelayMins){
            String trainDelayMinsDisp = formatDelayMinsToString(trainDelayMins);
            trainDelayMins_TV.setText(trainDelayMinsDisp);

            int delayColor = colorForDelayMins(trainDelayMins);
            if(delayColor != NO_COLOR){
                trainDelayMins_TV.setTextColor(delayColor);
                trainDueMins_TV.setTextColor(delayColor);
            }
        }

        public void setTrainDueTime(String trainDueTime){
            trainDueTime_TV.setText(trainDueTime);
        }

        public void doSomethingWithReminderBtn(){
            // TODO Something
        }

        private String formatDelayMinsToString(int delayMins){
            StringBuilder delayMinsDisplay = new StringBuilder();

            if(delayMins != 0){
                String sign = "";
                // Negative delay will already have a minus sign from API
                if(delayMins > 0){
                    sign = "+";
                }
                delayMinsDisplay.append("(");
                delayMinsDisplay.append(sign);
                delayMinsDisplay.append(delayMins);
                delayMinsDisplay.append(")");
            }

            return delayMinsDisplay.toString();
        }

        private int colorForDelayMins(int delayMins){
            Context ctx = mListener.getActivity();
            int colorId = NO_COLOR;
            if(delayMins < 0){
                // Train is early
                colorId = ctx.getResources().getColor(R.color.irishrailgreen);
            }
            else if(delayMins > 0 &&  delayMins < Train.MAJORDELAY_MINS){
                // Train has minor delay
                colorId = ctx.getResources().getColor(R.color.minordelay);
            }
            else if(delayMins >= Train.MAJORDELAY_MINS){
                // Train has major delay
                colorId = ctx.getResources().getColor(R.color.majordelay);
            }
            return colorId;

        }

    }
}
