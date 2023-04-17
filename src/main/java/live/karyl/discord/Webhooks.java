package live.karyl.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import live.karyl.DiscordFlows;
import live.karyl.config.ConfigManager;
import live.karyl.models.Container;
import live.karyl.models.FileContainer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Webhooks {

	private static final List<WebhookClient> clients = new ArrayList<>();
	private static final ConfigManager config = DiscordFlows.getConfig();
	public void init() {
		var tokens = config.getWebhookUrls();
		for (var token : tokens) {
			WebhookClientBuilder builder = new WebhookClientBuilder(token);
			builder.setThreadFactory((job) -> {
				Thread thread = new Thread(job);
				thread.setName("Webhook-Thread");
				thread.setDaemon(true);
				return thread;
			});
			builder.setWait(true);
			WebhookClient client = builder.build();
			clients.add(client);
		}
	}

	private static WebhookClient getClient() {
		return clients.get(new Random().nextInt(clients.size()));
	}

	public void sendStorageDiscord(Container container, FileContainer fileContainer) {
		var client = getClient();
		var file = fileContainer.getFile();
		var partNumber = container.getCurrentPart() + "/" + container.getTotalPart();

		System.out.println("Sending file " + partNumber + " to discord");

		client.send(file).whenComplete((message, throwable) -> {
			if (throwable != null) {
				throwable.printStackTrace();
			}
			String url = message.getAttachments().get(0).getUrl();
			fileContainer.setUrl(url);
			DiscordFlows.getPostgreSQL().addStorageFile(fileContainer, partNumber);
			if (container.getCurrentPart() == container.getTotalPart()) {
				System.out.println("Deleting folder cache");
				try {
					FileUtils.deleteDirectory(container.getTempFolder());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
