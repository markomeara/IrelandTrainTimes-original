package ie.markomeara.irelandtraintimes.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.ui.fragment.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.model.Train;

public class TrainViewHolder extends RecyclerView.ViewHolder {

    private static final int NO_COLOR = -1;

    private final View mTrainItem;
    @Bind(R.id.reminderButton)
    protected View mReminderBtn;
    @Bind(R.id.traindue_dest_TV)
    protected TextView mTrainDest_TV;
    @Bind(R.id.traindue_mins_TV)
    protected TextView mTrainDueMins_TV;
    @Bind(R.id.traindue_delay_TV)
    protected TextView mTrainDelayMins_TV;
    @Bind(R.id.traindue_time_TV)
    protected TextView mTrainDueTime_TV;

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
        mTrainDest_TV.setText(destination);
    }

    public void setTrainDueMins(int trainDueMins){
        mTrainDueMins_TV.setText(Integer.toString(trainDueMins) + " mins");
    }

    public void setTrainDelayMins(int trainDelayMins){
        String trainDelayMinsDisp = formatDelayMinsToString(trainDelayMins);
        mTrainDelayMins_TV.setText(trainDelayMinsDisp);

        int delayColor = colorForDelayMins(trainDelayMins);
        if(delayColor != NO_COLOR){
            mTrainDelayMins_TV.setTextColor(delayColor);
            mTrainDueMins_TV.setTextColor(delayColor);
        }
    }

    public void setTrainDueTime(String trainDueTime){
        mTrainDueTime_TV.setText(trainDueTime);
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