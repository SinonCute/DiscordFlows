package live.karyl.models;

public class FileListResponse {
	private String fileName;
	private String fileDownloadUri;
	private long size;

	public FileListResponse(String fileName, String fileDownloadUri, long size) {
		this.fileName = fileName;
		this.fileDownloadUri = fileDownloadUri;
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
