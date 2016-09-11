package ie.markomeara.irelandtraintimes.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import ie.markomeara.irelandtraintimes.R;

public class StationViewHolder extends RecyclerView.ViewHolder{

    private final View mStnItem;

    @Bind(R.id.stationName)
    TextView mStnName;

    @Bind(R.id.stationDistance)
    TextView mStnDistance;

    public StationViewHolder(View view) {
        super(view);
        mStnItem = view;
        ButterKnife.bind(this, view);
    }

    public View getView(){
        return mStnItem;
    }

    public void setStationDisplayName(String name){
        mStnName.setText(name);
    }

    public void setStnDistance(String distance){
        mStnDistance.setText(distance);
    }

}
