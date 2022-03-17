package net.devtech.arrp;

import net.devtech.arrp.api.RRPPreGenEntrypoint;
import net.devtech.arrp.impl.RuntimeResourcePackImpl;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ARRP implements PreLaunchEntrypoint {
	private static final Logger LOGGER = LoggerFactory.getLogger("BRRP");

	@Override
	public void onPreLaunch() {
		LOGGER.info("I used the json to destroy the json");
		FabricLoader loader = FabricLoader.getInstance();
		for (RRPPreGenEntrypoint entrypoint : loader.getEntrypoints("rrp:pregen", RRPPreGenEntrypoint.class)) {
			RuntimeResourcePackImpl.EXECUTOR_SERVICE.submit(entrypoint::pregen);
		}
	}
}
