package live.karyl.task;

import live.karyl.DiscordFlows;
import live.karyl.data.PostgreSQL;
import live.karyl.models.Container;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class UploadFile implements Runnable {

	private final Container container;
	private final PostgreSQL postgreSQL = DiscordFlows.getPostgreSQL();

	public UploadFile(Container container) {
		this.container = container;
		run();
	}

	@Override
	public void run() {
		var folder = container.getTempFolderChunked().listFiles();
		if (folder == null) return;
		for (var file : folder) {
			var partNumber = Integer.parseInt(file.getName().split("-")[1].split("\\.")[0]);
			container.setCurrentPart(partNumber);
			DiscordFlows.getWebhook().sendStorageDiscord(container, file);
		}
	}
}
