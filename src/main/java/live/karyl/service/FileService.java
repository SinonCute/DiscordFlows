package live.karyl.service;

import live.karyl.DiscordFlows;
import live.karyl.models.Container;
import live.karyl.models.FileContainer;
import live.karyl.task.ChunkFile;
import live.karyl.task.UploadFile;
import live.karyl.util.GenerateId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
public class FileService {

	@Autowired
	private ExecutorService taskExecutor;

	private Map<String, Container> containerMap = new HashMap<>();

	public String processFile(MultipartFile file, String userId) {
		File tempDir = new File("cache", GenerateId.generateId(10));
		var container = new Container(tempDir, userId, file.getOriginalFilename());
		File tempFile = new File(tempDir.getAbsolutePath(), file.getOriginalFilename());
		containerMap.put(container.getUuid(), container);

		try {
			// Save the uploaded file to the temporary directory
			file.transferTo(tempFile);

			taskExecutor.submit(() -> {
				new ChunkFile(container);
				new UploadFile(container);
				DiscordFlows.getPostgreSQL().addFile(container);
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return container.getUuid();
	}

	public InputStream combineStreams(List<FileContainer> fileContainerList) throws IOException {
		InputStream combinedStream = null;
		for (var fileContainer : fileContainerList) {
			URL url = new URL(fileContainer.getUrl());
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			if (combinedStream == null) {
				combinedStream = inputStream;
			} else {
				combinedStream = new SequenceInputStream(combinedStream, inputStream);
			}
		}
		return combinedStream;
	}

	public Container getContainer(String uuid) {
		return containerMap.get(uuid);
	}
}
