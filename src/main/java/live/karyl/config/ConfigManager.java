package live.karyl.config;

import live.karyl.DiscordFlows;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class ConfigManager {

	private static ConfigManager instance;

	private String databaseHost;
	private String databasePort;
	private String databaseName;
	private String databaseUser;
	private String databasePassword;
	private int databaseTimeout;

	private String tempDir;

	private List<String> webhookUrl;

	private String secretKey;

	public static ConfigManager getInstance() {
		return instance;
	}

	public void saveDefaultConfig() {
		if (!DiscordFlows.getDataFolder().exists()) {
			DiscordFlows.getDataFolder().mkdir();
		}

		File file = new File(DiscordFlows.getDataFolder(), "config.yml");

		if (!file.exists()) {
			try (InputStream in = DiscordFlows.getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void init() {
		saveDefaultConfig();
		File file = new File(DiscordFlows.getDataFolder(), "config.yml");
		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

		databaseHost = yaml.getString("database.host");
		databasePort = yaml.getString("database.port");
		databaseName = yaml.getString("database.name");
		databaseUser = yaml.getString("database.user");
		databasePassword = yaml.getString("database.password");
		databaseTimeout = yaml.getInt("database.timeout");

		tempDir = yaml.getString("tempDir");

		webhookUrl = yaml.getStringList("webhookUrl");

		secretKey = yaml.getString("secretKey");

		instance = this;
	}

	public String getDatabaseHost() {
		return databaseHost;
	}

	public String getDatabasePort() {
		return databasePort;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getDatabaseUser() {
		return databaseUser;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public int getDatabaseTimeout() { return databaseTimeout; }

	public String getTempDir() { return tempDir; }

	public List<String> getWebhookUrls() { return webhookUrl; }

	public String getSecretKey() { return secretKey; }
}
