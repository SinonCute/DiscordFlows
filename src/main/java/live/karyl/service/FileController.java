package live.karyl.service;


import live.karyl.DiscordFlows;
import live.karyl.models.FileListResponse;
import live.karyl.models.FileStatusResponse;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Controller
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")
public class FileController {

	@Autowired
	private FileService fileService;

	@PostMapping("/uploadFile")
	public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file, Authentication user) {

		if (file.getOriginalFilename() == null) {
			return ResponseEntity.badRequest().build();
		}

		String username = "anonymous";
		if (user != null) {
			username = user.name();
		}

		var uuid = fileService.processFile(file, username);

		return ResponseEntity.ok(uuid);
	}

	@PostMapping("/update")
	public ResponseEntity<?> updateFile(@RequestParam String id, @RequestParam String name) throws IOException {

		//TODO: update file name in database

		return ResponseEntity.ok().build();
	}

	@Async
	@GetMapping("/download")
	public CompletableFuture<ResponseEntity<InputStreamResource>> downloadFile(@RequestParam String id) throws IOException {
		var container = DiscordFlows.getPostgreSQL().getFile(id);
		var fileContainer = DiscordFlows.getPostgreSQL().getFiles(id);

		InputStream inputStream = fileService.combineStreams(fileContainer);
		InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentLength(container.getFileSize());
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + container.getFileName() + "\"");
		return CompletableFuture.completedFuture(new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK));
	}

	@PostMapping("/delete")
	public ResponseEntity<?> deleteFile(@RequestPart String id) {

		//TODO: delete file from database and discord

		return ResponseEntity.ok().build();
	}

	@GetMapping("/uploadedFiles")
	public FileListResponse[] getUploadedFiles() {
		var containers = DiscordFlows.getPostgreSQL().getAllFiles();
		containers.sort((file, file2) -> Long.compare(file2.getCreatedAt(), file.getCreatedAt()));

		List<FileListResponse> files = new ArrayList<>();
		for (var container : containers) {
			var fileId = container.getUuid();
			var fileName = container.getFileName();
			var fileSize = container.getFileSize();
			var downloadUrl = "/api/download?id=" + fileId;
			var file = new FileListResponse(fileName, downloadUrl, fileSize);
			files.add(file);
		}
		return files.toArray(new FileListResponse[0]);
	}

	@GetMapping("/fileStatus")
	public FileStatusResponse getFileStatus(@RequestParam String id) {
		var container = fileService.getContainer(id);
		return new FileStatusResponse(container.getFileName(), container.getCurrentPart(), container.getTotalPart());
	}
}
