package live.karyl.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import live.karyl.DiscordFlows;
import live.karyl.config.ConfigManager;

import java.sql.Connection;

public class PostgreSQL {

	private final ConfigManager config = DiscordFlows.getConfig();

	private static final String INSERT_ANIME = "INSERT INTO anime (anime_id, provider_id, provider_name) VALUES (?, ?, ?)";
	private static final String INSERT_STORAGE_FILE = "INSERT INTO storage_file (file_uuid, url, part_number) VALUES (?, ?, ?)";

	private HikariDataSource ds;

	public void init() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setJdbcUrl("jdbc:postgresql://%s:%s/%s"
				.formatted(config.getDatabaseHost(), config.getDatabasePort(), config.getDatabaseName()));
		hikariConfig.setUsername(config.getDatabaseUser());
		hikariConfig.setPassword(config.getDatabasePassword());
		hikariConfig.setConnectionTimeout(config.getDatabaseTimeout());
		hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
		hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
		this.ds = new HikariDataSource(hikariConfig);
	}

	public void addStorageFile(String fileUuid, String url, String partNumber) {
		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(INSERT_STORAGE_FILE);
			statement.setString(1, fileUuid);
			statement.setString(2, url);
			statement.setString(3, partNumber);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
