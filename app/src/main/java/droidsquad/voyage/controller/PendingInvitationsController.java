package droidsquad.voyage.controller;

import droidsquad.voyage.activity.PendingInvitationsActivity;
import droidsquad.voyage.model.VoyageUser;

/**
 * Created by Vivian on 2/12/2016.
 */
public class PendingInvitationsController {

    private PendingInvitationsActivity activity;
    private VoyageUser user;

    public PendingInvitationsController(PendingInvitationsActivity activity){
        this.activity = activity;
        this.user = new VoyageUser();
    }

}
