package ie.markomeara.irelandtraintimes.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "ArrayOfObjStationData")
public class StationList {

    @ElementList(inline = true)
    private List<Station> mStations;

    public List<Station> getStationList(){
        return this.mStations;
    }

}
