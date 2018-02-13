package com.sixsq.slipstream.event;

import org.restlet.Context;
import org.restlet.routing.Router;

public class EventRouter extends Router {

    public EventRouter(Context context) {
        super(context);

        attach("", EventListResource.class);
        attach("/", EventListResource.class);
    }

}
