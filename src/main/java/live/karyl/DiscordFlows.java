package live.karyl;

import live.karyl.config.ConfigManager;
import live.karyl.data.PostgreSQL;
import live.karyl.discord.Webhooks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class DiscordFlows {

	private static ConfigManager config;
	private static PostgreSQL postgreSQL;
	private static Webhooks webhook;

	public static void main(String[] args) {

		config = new ConfigManager();
		config.init();

		postgreSQL = new PostgreSQL();
		postgreSQL.init();

		webhook = new Webhooks();
		webhook.init();

		SpringApplication.run(DiscordFlows.class, args);
	}

	public static ConfigManager getConfig() {
		return config;
	}

	public static PostgreSQL getPostgreSQL() {
		return postgreSQL;
	}

	public static Webhooks getWebhook() {
		return webhook;
	}

	public static File getDataFolder() {
		Path path = Paths.get(System.getProperty("user.dir"), "config");
		File file = path.toFile();
		if (!file.exists()) file.mkdirs();
		return file;
	}

	public static File getCacheFolder() {
		File cacheFolder = new File("cache");
		if (!cacheFolder.exists()) cacheFolder.mkdirs();
		return cacheFolder;
	}

	public static InputStream getResourceAsStream(String name) {
		return DiscordFlows.class.getClassLoader().getResourceAsStream(name);
	}
}
