package ie.markomeara.irelandtraintimes.views;

import ie.markomeara.irelandtraintimes.views.adapters.TrainsDueListAdapter;
import ie.markomeara.irelandtraintimes.views.TrainListItem;

/**
 * Created by Mark on 16/11/2014.
 */
public class TrainListHeader implements TrainListItem {

    private String headingText;

    public TrainListHeader(String direction){
        this.headingText = direction;
    }

    @Override
    public int getListViewType() {
        return TrainsDueListAdapter.RowType.HEADER.ordinal();
    }

    public void setHeadingText(String text){
        this.headingText = text;
    }

    public String getHeadingText(){
        return this.headingText;
    }
}
