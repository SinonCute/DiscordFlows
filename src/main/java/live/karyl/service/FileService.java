package live.karyl.service;

import live.karyl.models.Container;
import live.karyl.task.ChunkFile;
import live.karyl.task.UploadFile;
import live.karyl.util.GenerateId;
import org.apache.commons.io.FileUtils;
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

	public void processFile(MultipartFile file) {
		File tempDir = new File("cache", GenerateId.generateId(10));
		var container = new Container(tempDir, file.getOriginalFilename());
		File tempFile = new File(tempDir.getAbsolutePath(), file.getOriginalFilename());

		try {
			// Save the uploaded file to the temporary directory
			file.transferTo(tempFile);

			taskExecutor.submit(() -> {
				new ChunkFile(container);
				new UploadFile(container);
				try {
					FileUtils.deleteDirectory(tempDir);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
