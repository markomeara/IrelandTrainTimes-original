package ie.markomeara.irelandtraintimes.model;

import ie.markomeara.irelandtraintimes.adapter.TrainsDueRecyclerViewAdapter;

public class TrainListHeader implements TrainListItem {

    private String mHeadingText;

    public TrainListHeader(String direction){
        this.mHeadingText = direction;
    }

    @Override
    public int getViewType() {
        return TrainsDueRecyclerViewAdapter.RowType.HEADER.ordinal();
    }

    public void setHeadingText(String text){
        this.mHeadingText = text;
    }

    public String getHeadingText(){
        return this.mHeadingText;
    }
}
