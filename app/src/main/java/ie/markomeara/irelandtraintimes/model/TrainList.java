package ie.markomeara.irelandtraintimes.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by markomeara on 30/01/2016.
 */
@Root(name = "ArrayOfObjStationData")
public class TrainList {

    @ElementList(inline = true, required = false, empty = false)
    List<Train> trains;

    public List<Train> getTrainList(){
        return this.trains;
    }
}
