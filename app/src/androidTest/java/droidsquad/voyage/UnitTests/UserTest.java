package droidsquad.voyage.UnitTests;

import org.junit.Before;
import org.junit.Test;

import droidsquad.voyage.model.objects.User;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Zemei on 3/8/2016.
 */
public class UserTest {

    User user1, user2, user3;

    @Before
    public void setUp() {
        user1 = new User("123", "FirstName", "LastName");
        user2 = new User("123", "Joe", "Smith");
        user3 = new User("345", "Joe", "Smith");
    }

    @Test
    public void testEquals() {
        assertEquals(user1.equals(user2), true); // both users have same id
        assertEquals(user2.equals(user1), true);

        assertEquals(user1.equals(user3), false); // both users have different id's
        assertEquals(user3.equals(user1), false);

        assertEquals(user2.equals(user3), false); // both users have same name, different id
        assertEquals(user3.equals(user2), false);

        assertEquals(user1.equals(user1), true); // check identity case
        assertEquals(user2.equals(user2), true);
        assertEquals(user3.equals(user3), true);
    }

    @Test
    public void testToString() {
        assertEquals(user1.toString(), "User: FirstName LastName");
        assertEquals(user2.toString(), "User: Joe Smith");
        assertEquals(user3.toString(), "User: Joe Smith");
    }
}
