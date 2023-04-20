package live.karyl.service;

import live.karyl.DiscordFlows;
import live.karyl.models.FileStorage;
import live.karyl.task.UploadFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;

@Service
public class FileService {

	@Autowired
	private ExecutorService taskExecutor;

	public void processFile(MultipartFile file, String uuid) {
		var cacheFolder = DiscordFlows.getCacheFolder();
		var fileCache = new File(cacheFolder.getAbsolutePath(), uuid);
		var fileStorage = new FileStorage(uuid, file.getOriginalFilename());
		fileStorage.setFileSize(file.getSize());
		try {
			file.transferTo(fileCache);
			taskExecutor.submit(() -> {
				new UploadFile(fileStorage);
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
