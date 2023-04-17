package live.karyl.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import live.karyl.DiscordFlows;
import live.karyl.config.ConfigManager;
import live.karyl.models.Container;
import live.karyl.models.FileContainer;

import java.util.ArrayList;
import java.util.List;

public class PostgreSQL {

	private final ConfigManager config = DiscordFlows.getConfig();

	private static final String INSERT_FILE = "INSERT INTO file (file_owner, file_uuid, file_name, file_size) VALUES (?, ?, ?, ?)";
	private static final String INSERT_STORAGE_FILE = "INSERT INTO storage_file (file_uuid, url, part_number) VALUES (?, ?, ?)";

	private static final String SELECT_ALL_FILE = "SELECT * FROM file";
	private static final String SELECT_FILE = "SELECT * FROM file WHERE file_uuid = ?";
	private static final String SELECT_STORAGE_FILE = "SELECT * FROM storage_file WHERE file_uuid = ?";

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

	public void addStorageFile(FileContainer fileContainer, String partNumber) {
		var fileUuid = fileContainer.getUuid();
		var url = fileContainer.getUrl();

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

	public void addFile(Container container) {
		var fileUuid = container.getUuid();
		var fileName = container.getFileName();
		var fileOwner = container.getFileOwner();
		var fileSize = container.getFileSize();

		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(INSERT_FILE);
			statement.setString(1, fileOwner);
			statement.setString(2, fileUuid);
			statement.setString(3, fileName);
			statement.setLong(4, fileSize);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Container getFile(String fileUUID) {
		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(SELECT_FILE);
			statement.setString(1, fileUUID);
			var resultSet = statement.executeQuery();
			if (resultSet.next()) {
				var container = new Container();
				container.setUuid(resultSet.getString("file_uuid"));
				container.setFileOwner(resultSet.getString("file_owner"));
				container.setFileName(resultSet.getString("file_name"));
				container.setFileSize(resultSet.getLong("file_size"));
				return container;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<FileContainer> getFiles(String fileUUID) {
		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(SELECT_STORAGE_FILE);
			statement.setString(1, fileUUID);
			var resultSet = statement.executeQuery();
			List<FileContainer> fileContainers = new ArrayList<>();
			while (resultSet.next()) {
				var fileContainer = new FileContainer();
				fileContainer.setUuid(resultSet.getString("file_uuid"));
				fileContainer.setUrl(resultSet.getString("url"));
				fileContainer.setPartNumber(Integer.parseInt(resultSet.getString("part_number").split("/")[0]));
				fileContainers.add(fileContainer);
			}
			return fileContainers;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Container> getAllFiles() {
		try (var connection = ds.getConnection()) {
			var statement = connection.prepareStatement(SELECT_ALL_FILE);
			var resultSet = statement.executeQuery();
			List<Container> containers = new ArrayList<>();
			while (resultSet.next()) {
				var container = new Container();
				container.setUuid(resultSet.getString("file_uuid"));
				container.setFileOwner(resultSet.getString("file_owner"));
				container.setFileName(resultSet.getString("file_name"));
				container.setFileSize(resultSet.getLong("file_size"));
				container.setCreatedAt(resultSet.getTimestamp("created_at").getTime());
				containers.add(container);
			}
			return containers;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
