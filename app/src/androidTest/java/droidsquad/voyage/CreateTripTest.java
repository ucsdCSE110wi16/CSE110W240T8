package droidsquad.voyage;
//import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import droidsquad.voyage.activity.CreateTripActivity;
import droidsquad.voyage.model.ParseModel;
import droidsquad.voyage.model.ParseTripModel;
import droidsquad.voyage.model.Trip;

/**
 * Created by gumbe on 1/23/2016.
 */
public class CreateTripTest {

    CreateTripActivity tripActivity;
    Trip completeTrip;
    Trip simpleTrip;

    @Before
    public void setUp() throws Exception {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 05, 10);
        Date from = calendar.getTime();
        calendar.set(2015, 05, 17);
        Date to = calendar.getTime();

       // completeTrip = new Trip( "Complete", "La Jolla", "San Diego", false, 5,
         //                                                       from, to, "Car" );
       // simpleTrip = new Trip( "Simple", "Curitiba", "La Jolla", true );
    }

    @Test
    public void save(){
        ParseTripModel.saveTrip(completeTrip);
        ParseTripModel.saveTrip(simpleTrip);
    }

}
