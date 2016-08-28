package ie.markomeara.irelandtraintimes.viewholder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.R;
import ie.markomeara.irelandtraintimes.ui.fragment.StationNextTrainsFragment;
import ie.markomeara.irelandtraintimes.model.Train;

public class TrainViewHolder extends RecyclerView.ViewHolder {

    private static final int NO_COLOR = -1;

    private final View mTrainItem;
    @Bind(R.id.traindue_statusimg)
    protected ImageView mTrainStatusImg;
    @Bind(R.id.traindue_dest_TV)
    protected TextView mTrainDest_TV;
    @Bind(R.id.traindue_mins_TV)
    protected TextView mTrainDueMins_TV;
    @Bind(R.id.traindue_delay_TV)
    protected TextView mTrainDelayMins_TV;
    @Bind(R.id.traindue_time_TV)
    protected TextView mTrainDueTime_TV;

    private Context mContext;

    private final StationNextTrainsFragment mListener;

    public TrainViewHolder(View view, StationNextTrainsFragment listener) {
        super(view);
        mTrainItem = view;
        ButterKnife.bind(this, view);
        mListener = listener;
        mContext = mTrainItem.getContext();
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
        colorForDelayMins(trainDelayMins);
    }

    public void setTrainDueTime(String trainDueTime){
        mTrainDueTime_TV.setText(trainDueTime);
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

    private void colorForDelayMins(int delayMins){
        if(delayMins <= 0){
            // Train is early or on time
            mTrainStatusImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.trainstatus_ontime_circle));
            mTrainDueMins_TV.setTextColor(mContext.getResources().getColor(R.color.ontime));
        }
        else if(delayMins > 0 &&  delayMins < Train.MAJORDELAY_MINS){
            mTrainStatusImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.trainstatus_minordelay_circle));
            mTrainDueMins_TV.setTextColor(mContext.getResources().getColor(R.color.minordelay));

        }
        else if(delayMins >= Train.MAJORDELAY_MINS){
            mTrainStatusImg.setBackground(ContextCompat.getDrawable(mContext, R.drawable.trainstatus_majordelay_circle));
            mTrainDueMins_TV.setTextColor(mContext.getResources().getColor(R.color.majordelay));
        }

    }

}