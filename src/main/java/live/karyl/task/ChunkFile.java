package live.karyl.task;

import live.karyl.models.Container;

import java.io.*;

public class ChunkFile implements Runnable {


	private final int CHUNK_SIZE = 1024 * 1024 * 20;
	private final Container container;

	public ChunkFile(Container container) {
		this.container = container;
		run();
	}

	@Override
	public void run() {
		var folder = container.getTempFolder().listFiles();
		if (folder == null) return;


		for (var file : folder) {
			if (file.length() > CHUNK_SIZE) {
				byte[] buffer = new byte[CHUNK_SIZE];
				int bytesRead;
				int chunkNumber = 1;

				try (InputStream inputStream = new FileInputStream(file)) {
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						var chunkedFolder = container.getTempFolderChunked();
						File chunkFile = new File(chunkedFolder.getAbsolutePath(), file.getName() + "-" + chunkNumber + ".part");
						try (OutputStream outputStream = new FileOutputStream(chunkFile)) {
							outputStream.write(buffer, 0, bytesRead);
						}
						chunkNumber++;
					}
					container.setTotalPart(chunkNumber);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
