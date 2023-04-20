package live.karyl.models;

public class FileStorage {
	private String uuid;
	private String fileName;
	private String fileUrl;

	private long fileSize;

	private long createdAt;

	public FileStorage(String uuid, String fileName) {
		this.uuid = uuid;
		this.fileName = fileName;
	}

	public FileStorage() {}

	public String getUuid() {
		return uuid;
	}

	public String getFileName() { return fileName; }

	public void setUuid(String uuid) { this.uuid = uuid; }

	public String getFileUrl() { return fileUrl;}

	public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

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
