package ie.markomeara.irelandtraintimes.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by markomeara on 30/01/2016.
 */
@Root(name = "ArrayOfObjStationData")
public class StationList {

    @ElementList(inline = true)
    List<Station> stations;

    public List<Station> getStationList(){
        return this.stations;
    }

}
