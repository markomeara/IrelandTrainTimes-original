package ie.markomeara.irelandtraintimes.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "ArrayOfObjStationData")
public class TrainList {

    @ElementList(inline = true, required = false, empty = false)
    private List<Train> mTrains;

    public List<Train> getTrainList(){
        return this.mTrains;
    }
}
