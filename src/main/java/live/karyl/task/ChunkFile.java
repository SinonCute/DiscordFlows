package live.karyl.task;

import live.karyl.DiscordFlows;
import live.karyl.config.ConfigManager;
import live.karyl.models.Container;
import live.karyl.models.FileContainer;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.UUID;

public class ChunkFile implements Runnable {


	private final int CHUNK_SIZE = 1024 * 1024 * 20;
	private final Container container;
	private final ConfigManager config = DiscordFlows.getConfig();

	public ChunkFile(Container container) {
		this.container = container;
		run();
	}

	@Override
	public void run() {
		var folder = container.getTempFolder().listFiles();
		if (folder == null) return;
		var file = folder[0];
		var chunkedFolder = container.getTempFolderChunked();
		var fileUUID = container.getUuid();
		container.setFileSize(file.length());

		if (file.length() > CHUNK_SIZE) {
			byte[] buffer = new byte[CHUNK_SIZE];
			int bytesRead;
			int chunkNumber = 1;

			try (InputStream inputStream = new FileInputStream(file)) {
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					var uuid = UUID.randomUUID().toString();
					var chunkFile = new File(chunkedFolder.getAbsolutePath(), uuid);
					var fileContainer = new FileContainer(fileUUID, chunkFile, chunkNumber);
					try (OutputStream outputStream = new FileOutputStream(chunkFile)) {
						outputStream.write(buffer, 0, bytesRead);
					}
					container.addChunk(fileContainer);
					chunkNumber++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				var uuid = UUID.randomUUID().toString();
				var chunkFile = new File(chunkedFolder.getAbsolutePath(), uuid);
				var fileContainer = new FileContainer(fileUUID, chunkFile, 1);
				var value = FileUtils.readFileToByteArray(file);
				FileUtils.writeByteArrayToFile(chunkFile, value, false);
				container.addChunk(fileContainer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Finished chunking file");
	}
}
