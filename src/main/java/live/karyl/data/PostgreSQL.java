package live.karyl.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import live.karyl.DiscordFlows;
import live.karyl.config.ConfigManager;
import live.karyl.models.FileStorage;

import java.util.ArrayList;
import java.util.List;

public class PostgreSQL {

	private final ConfigManager config = DiscordFlows.getConfig();

	private static final String INSERT_FILE = "INSERT INTO file (file_uuid, file_size, file_url) VALUES (?, ?, ?)";

	private static final String SELECT_ALL_FILE = "SELECT * FROM file";
	private static final String SELECT_FILE = "SELECT * FROM file WHERE file_uuid = ?";

    private static final String DELETE_FILE = "DELETE FROM file WHERE file_uuid = ?";

	private static final String CLEAR = "DELETE FROM file";

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

	public void addFile(FileStorage container) {
		var fileUuid = container.getUuid();
		var fileSize = container.getFileSize();
		var fileUrl = container.getFileUrl();

		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(INSERT_FILE);
			statement.setString(1, fileUuid);
			statement.setLong(2, fileSize);
			statement.setString(3, fileUrl);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public FileStorage getFile(String fileUUID) {
		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(SELECT_FILE);
			statement.setString(1, fileUUID);
			var resultSet = statement.executeQuery();
			if (resultSet.next()) {
				var container = new FileStorage();
				container.setUuid(resultSet.getString("file_uuid"));
				container.setFileSize(resultSet.getLong("file_size"));
				container.setFileUrl(resultSet.getString("file_url"));
				return container;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<FileStorage> getAllFiles() {
		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(SELECT_ALL_FILE);
			var resultSet = statement.executeQuery();
			List<FileStorage> containers = new ArrayList<>();
			while (resultSet.next()) {
				var container = new FileStorage();
				container.setUuid(resultSet.getString("file_uuid"));
				container.setFileSize(resultSet.getLong("file_size"));
				container.setFileUrl(resultSet.getString("file_url"));
				container.setCreatedAt(resultSet.getTimestamp("created_at").getTime());
				containers.add(container);
			}
			return containers;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteFile(String uuid) {
		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(DELETE_FILE);
			statement.setString(1, uuid);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clear() {
		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(CLEAR);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
