package live.karyl.models;

import java.io.File;

public class FileContainer {
	private String uuid;
	private File file;
	private String url;
	private int partNumber;

	public FileContainer(String uuid, File file, int partNumber) {
		this.uuid = uuid;
		this.file = file;
		this.partNumber = partNumber;
	}

	public FileContainer() {}

	public String getUuid() {
		return uuid;
	}

	public File getFile() {
		return file;
	}

	public int getPartNumber() {
		return partNumber;
	}

	public String getUrl() {
		return url;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
