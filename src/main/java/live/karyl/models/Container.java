package live.karyl.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Container {
	private String uuid;
	private String fileOwner;
	private String fileName;

	private File tempFolder;
	private File tempFolderChunked;

	private List<FileContainer> chunks;

	private int currentPart;
	private int totalPart;

	private long fileSize;

	private long createdAt;

	public Container(File tempFolder, String fileOwner, String fileName) {
		if (!tempFolder.exists()) tempFolder.mkdirs();
		this.tempFolder = tempFolder;
		this.fileOwner = fileOwner;
		this.fileName = fileName;
		this.uuid = UUID.randomUUID().toString();
		chunks = new ArrayList<>();
	}

	public Container() {}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) { this.uuid = uuid; }

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) { this.fileName = fileName; }

	public String getFileOwner() { return fileOwner; }

	public void setFileOwner(String fileOwner) { this.fileOwner = fileOwner; }

	public File getTempFolder() {
		return tempFolder;
	}

	public File getTempFolderChunked() {
		if (tempFolderChunked == null) {
			tempFolderChunked = new File(tempFolder + "/chunked");
			if (!tempFolderChunked.exists()) tempFolderChunked.mkdirs();
		}
		return tempFolderChunked;
	}

	public int getCurrentPart() {
		return currentPart;
	}

	public void setCurrentPart(int currentPart) {
		this.currentPart = currentPart;
	}

	public List<FileContainer> getChunks() {
		return chunks;
	}

	public void addChunk(FileContainer chunk) {
		this.chunks.add(chunk);
		System.out.println("Added chunk " + chunk.getPartNumber() + " of " + totalPart);
		totalPart++;
	}

	public int getTotalPart() {
		return totalPart;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long totalSize) {
		this.fileSize = totalSize;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}
}
