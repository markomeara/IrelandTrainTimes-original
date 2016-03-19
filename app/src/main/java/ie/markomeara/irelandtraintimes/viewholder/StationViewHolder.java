package ie.markomeara.irelandtraintimes.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.R;

/**
 * Created by markomeara on 19/03/2016.
 */
public class StationViewHolder extends RecyclerView.ViewHolder{

    private final View stnItem;
    @Bind(R.id.stationName)
    TextView stnName;
    @Bind(R.id.stationDistance)
    TextView stnDistance;

    public StationViewHolder(View view) {
        super(view);
        stnItem = view;
        ButterKnife.bind(this, view);
    }

    public View getView(){
        return stnItem;
    }

    public void setStationDisplayName(String name){
        stnName.setText(name);
    }

    public void setStnDistance(String distance){
        stnDistance.setText(distance);
    }

}
