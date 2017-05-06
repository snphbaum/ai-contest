package org.byteforce.server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;


public class RestApplication extends Application {
	private final Set<Object> singletons = new HashSet<>();

	public RestApplication() {
		singletons.add(new GameServerImpl());
	}

	@Override
	public Set<Object> getSingletons() {
		return Collections.unmodifiableSet(singletons);
	}
}