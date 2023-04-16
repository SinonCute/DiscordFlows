package live.karyl.service;


import live.karyl.models.FileListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Controller
@RestController
@RequestMapping("/api")
@PreAuthorize("hasRole('USER')")
public class FileController {

	@Autowired
	private FileService fileService;

	@PostMapping("/uploadFile")
	public ResponseEntity<?> uploadFile(@RequestPart MultipartFile file) {

		if (file.getOriginalFilename() == null) {
			return ResponseEntity.badRequest().build();
		}

		fileService.processFile(file);
		//how to send back status process?

		return ResponseEntity.ok().build();
	}

	@PostMapping("/update")
	public ResponseEntity<?> updateFile(@RequestParam String id, @RequestParam String name) throws IOException {

		//TODO: update file name in database

		return ResponseEntity.ok().build();
	}

	@PostMapping("/download")
	public ResponseEntity<?> downloadFile(@RequestParam String id) {


		return ResponseEntity.ok().build();
	}

	@PostMapping("/delete")
	public ResponseEntity<?> deleteFile(@RequestPart String id) {

		//TODO: delete file from database and discord

		return ResponseEntity.ok().build();
	}

	@GetMapping("/uploadedFiles")
	public FileListResponse[] getUploadedFiles() {
		var files = new FileListResponse[2];
		files[0] = new FileListResponse("test", "test", "test", 10000000);
		files[1] = new FileListResponse("test", "test", "test", 1);
		return files;
	}
}
