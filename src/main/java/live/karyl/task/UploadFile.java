package live.karyl.task;

import live.karyl.DiscordFlows;
import live.karyl.models.FileStorage;

import java.io.File;

public class UploadFile implements Runnable {

	private final FileStorage container;
	private final File cacheFolder = DiscordFlows.getCacheFolder();

	public UploadFile(FileStorage fileStorage) {
		this.container = fileStorage;
		run();
	}

	@Override
	public void run() {
		if (!cacheFolder.exists()) return;
		for (var file : cacheFolder.listFiles()) {
			if (!file.getName().equals(container.getUuid())) continue;
			System.out.println("Uploading" + file.getName() + " to Discord");
			DiscordFlows.getWebhook().sendStorageDiscord(container, file);
		}
	}
}
