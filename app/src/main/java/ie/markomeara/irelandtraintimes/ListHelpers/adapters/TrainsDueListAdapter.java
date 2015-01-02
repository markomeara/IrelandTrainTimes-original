package ie.markomeara.irelandtraintimes.ListHelpers.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ie.markomeara.irelandtraintimes.ListHelpers.TrainListHeader;
import ie.markomeara.irelandtraintimes.ListHelpers.interfaces.TrainListItem;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.Train;

/**
 * Created by Mark on 16/11/2014.
 */
public class TrainsDueListAdapter extends ArrayAdapter<TrainListItem> {

    private static final String TAG = TrainsDueListAdapter.class.getSimpleName();

    private LayoutInflater inflater;

    public enum RowType {
        TRAIN, HEADER
    }


    public TrainsDueListAdapter(Context context, List<TrainListItem> trains) {
        super(context, 0, trains);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        headerView = inflater.inflate(R.layout.list_trains_header, parent, false);

        TextView headerTV = (TextView) headerView.findViewById(R.id.trainsDueListHeader);
        headerTV.setText(header.getHeadingText());
        return headerView;
    }

    private View getTrainItemView(final Train train, ViewGroup parent){
        View trainDetailsView = inflater.inflate(R.layout.list_trains_item, parent, false);

        View detailsContainer = trainDetailsView.findViewById(R.id.traindue_detailscontainer_RL);

        TextView trainDest_TV = (TextView) trainDetailsView.findViewById(R.id.traindue_dest_TV);
        TextView trainDueMins_TV = (TextView) trainDetailsView.findViewById(R.id.traindue_mins_TV);
        TextView trainDueTime_TV = (TextView) trainDetailsView.findViewById(R.id.traindue_time_TV);
        TextView trainDelayMins_TV = (TextView) trainDetailsView.findViewById(R.id.traindue_delay_TV);
        ImageButton reminderBtn = (ImageButton) trainDetailsView.findViewById(R.id.reminderButton);

        detailsContainer.setOnClickListener(new ListView.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Show details for: " + train.getDestination());
            }
        });

        reminderBtn.setOnClickListener(new ListView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Reminder button: " + train.getDestination());
            }
        });

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

        return trainDetailsView;
    }

    private void reminderClicked(View v){

    }
}
