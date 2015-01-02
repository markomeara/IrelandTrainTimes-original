package ie.markomeara.irelandtraintimes.ListHelpers;

import ie.markomeara.irelandtraintimes.ListHelpers.adapters.TrainsDueListAdapter;
import ie.markomeara.irelandtraintimes.ListHelpers.interfaces.TrainListItem;

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
