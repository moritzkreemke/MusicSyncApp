package com.moritz.musicsyncapp.controller.session;

import com.moritz.musicsyncapp.model.client.IClient;
import com.moritz.musicsyncapp.model.session.ISession;

public interface ISessionController {

    ISession create(IClient owner);
    ISession join(IClient client);
    ISession getSession();

}
