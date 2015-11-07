package ie.markomeara.irelandtraintimes.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import ie.markomeara.irelandtraintimes.views.TrainListHeader;
import ie.markomeara.irelandtraintimes.views.TrainListItem;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.model.Train;

/**
 * Created by Mark on 16/11/2014.
 */
public class TrainsDueListAdapter extends ArrayAdapter<TrainListItem> {

    private static final String TAG = TrainsDueListAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private Activity callingActivity;

    private OnClickListener trainDetailsOnClickListener;
    private OnClickListener reminderButtonOnClickListener;

    public enum RowType {
        TRAIN, HEADER
    }

    public TrainsDueListAdapter(Activity activity, List<TrainListItem> trains) {
        super(activity, 0, trains);
        this.callingActivity = activity;
        inflater = (LayoutInflater) callingActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getListViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Use convertView if not null
        TrainListItem listItem = getItem(position);
        View listItemView;
        if(listItem instanceof TrainListHeader){
            listItemView = getHeaderView((TrainListHeader) listItem, parent);
        }
        else{
            listItemView = getTrainItemView((Train) listItem, parent);
        }
        return listItemView;
    }

    private View getHeaderView(TrainListHeader header, ViewGroup parent){
        View headerView;

        headerView = inflater.inflate(R.layout.list_header_trains, parent, false);

        TextView headerTV = (TextView) headerView.findViewById(R.id.trainsDueListHeader);
        headerTV.setText(header.getHeadingText());
        return headerView;
    }

    private View getTrainItemView(final Train train, ViewGroup parent){
        View trainDetailsView = inflater.inflate(R.layout.list_item_trains, parent, false);

        View detailsContainer = trainDetailsView.findViewById(R.id.traindue_detailscontainer_RL);

        TextView trainDest_TV = (TextView) trainDetailsView.findViewById(R.id.traindue_dest_TV);
        TextView trainDueMins_TV = (TextView) trainDetailsView.findViewById(R.id.traindue_mins_TV);
        TextView trainDueTime_TV = (TextView) trainDetailsView.findViewById(R.id.traindue_time_TV);
        TextView trainDelayMins_TV = (TextView) trainDetailsView.findViewById(R.id.traindue_delay_TV);
        ImageButton reminderBtn = (ImageButton) trainDetailsView.findViewById(R.id.reminderButton);

        if(trainDetailsOnClickListener != null) {
            detailsContainer.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    trainDetailsOnClickListener.onClick(train);
                }
            });
        }

        if(reminderButtonOnClickListener != null){
            reminderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reminderButtonOnClickListener.onClick(train);
                }
            });
        }

        trainDest_TV.setText(train.getDestination());
        trainDueMins_TV.setText(Integer.toString(train.getDueIn()) + " mins");
        trainDueTime_TV.setText(train.getExpDepart());

        int delayMins = train.getDelayMins();

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

        trainDelayMins_TV.setText(delayMinsDisplay.toString());

        if(delayMins < 0){
            int earlyColorId = getContext().getResources().getColor(R.color.irishrailgreen);
            trainDueMins_TV.setTextColor(earlyColorId);
            trainDelayMins_TV.setTextColor(earlyColorId);
        }
        else if(delayMins > 0 &&  delayMins < Train.MAJORDELAY_MINS){
            int minorDelayColorId = getContext().getResources().getColor(R.color.minordelay);
            trainDueMins_TV.setTextColor(minorDelayColorId);
            trainDelayMins_TV.setTextColor(minorDelayColorId);
        }
        else if(delayMins >= Train.MAJORDELAY_MINS){
            int majorDelayColorId = getContext().getResources().getColor(R.color.majordelay);
            trainDueMins_TV.setTextColor(majorDelayColorId);
            trainDelayMins_TV.setTextColor(majorDelayColorId);
        }

        return trainDetailsView;
    }

    public void setTrainDetailsOnClickListener(OnClickListener onClickListener){
        this.trainDetailsOnClickListener = onClickListener;
    }

    public void setReminderButtonOnClickListener(OnClickListener onClickListener){
        this.reminderButtonOnClickListener = onClickListener;
    }

    public interface OnClickListener{
        public void onClick(Train train);
    }
}
