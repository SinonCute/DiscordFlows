package live.karyl.service;


import jakarta.servlet.http.HttpServletRequest;
import live.karyl.DiscordFlows;
import live.karyl.data.PostgreSQL;
import live.karyl.models.StorageResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@Controller
@RestController
@RequestMapping("/api")
public class FileController {

	@Autowired
	FileService fileService;

	private final PostgreSQL postgreSQL = DiscordFlows.getPostgreSQL();

	/*
	 * Method: SIZE, CLEAR, FULL-SIZE, SAVE
	 */
	@PostMapping("storage/**")
	public StorageResponse queryStorage(HttpServletRequest request, MultipartFile file) {
		var fileId = request.getRequestURI().replace("/api/storage/", "");
		var method = request.getHeader("x-method");
		if (method == null || fileId == null) {
			return new StorageResponse(true, "Missing header or file id", 0);
		}
		System.out.println("Method: " + method);
		switch (method) {
			case "SIZE" -> {
				var fileSize = postgreSQL.getFile(fileId).getFileSize();
				return new StorageResponse(false, "File size retrieved", fileSize);
			}
			case "FULL-SIZE" -> {
				var listFile = postgreSQL.getAllFiles();
				var totalSize = 0;
				for (var fileStorage : listFile) {
					totalSize += fileStorage.getFileSize();
				}
				return new StorageResponse(false, "Total size retrieved", totalSize);
			}
			case "CLEAR" -> {
				postgreSQL.clear();
				return new StorageResponse(false, "Storage cleared", 0);
			}
			case "SAVE" -> {
				fileService.processFile(file, fileId);
				return new StorageResponse(false, "File saved", 0);
			}
		}
		return new StorageResponse(true, "Invalid method", 0);
	}

	/*
	 * Method: DELETE
	 */
	@DeleteMapping("storage/**")
	public StorageResponse deleteStorage(HttpServletRequest request) {
		var fileId = request.getRequestURI().replace("/api/storage/", "");
		if (fileId == null) {
			return new StorageResponse(true, "Missing file id", 0);
		}
		postgreSQL.deleteFile(fileId);
		return new StorageResponse(false, "File deleted", 0);
	}

	/*
	 * Method: GET
	 */
	@GetMapping("storage/**")
	public ResponseEntity<InputStreamResource> getStorage(HttpServletRequest request) {
		var fileId = request.getRequestURI().replace("/api/storage/", "");
		if (fileId == null) {
			return ResponseEntity.badRequest().build();
		}
		var file = postgreSQL.getFile(fileId);
		OkHttpClient client = new OkHttpClient();
		Request requestFile = new Request.Builder()
				.url(file.getFileUrl())
				.build();
		try {
			var response = client.newCall(requestFile).execute();
			InputStream is = response.body().byteStream();
			InputStreamResource inputStreamResource = new InputStreamResource(is);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentLength(file.getFileSize());
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().build();
	}
}
