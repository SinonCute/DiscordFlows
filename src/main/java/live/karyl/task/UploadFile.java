package live.karyl.task;

import live.karyl.DiscordFlows;
import live.karyl.data.PostgreSQL;
import live.karyl.models.Container;

public class UploadFile implements Runnable {

	private final Container container;
	private final PostgreSQL postgreSQL = DiscordFlows.getPostgreSQL();

	public UploadFile(Container container) {
		this.container = container;
		run();
	}

	@Override
	public void run() {
		int partNumber = 1;
		for (var file : container.getChunks()) {
			System.out.println("Uploading part " + partNumber + " of " + container.getChunks().size());
			container.setCurrentPart(partNumber);
			DiscordFlows.getWebhook().sendStorageDiscord(container, file);
			partNumber++;
		}
	}
}
