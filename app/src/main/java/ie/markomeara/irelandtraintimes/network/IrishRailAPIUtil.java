package ie.markomeara.irelandtraintimes.network;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ie.markomeara.irelandtraintimes.model.Train;

/**
 * Created by mark on 16/03/15.
 */
public class IrishRailAPIUtil {

    public static Train extractTrainFromTrainList(String trainCode, List<Train> trainList) throws ParserConfigurationException, SAXException, IOException {
        Train relevantTrain = null;

        for(Train train : trainList){
            if(train.getTrainCode().equals(trainCode)){
                relevantTrain = train;
                break;
            }
        }

        return relevantTrain;

    }
}
