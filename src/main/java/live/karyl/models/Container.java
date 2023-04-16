package live.karyl.models;

import java.io.File;
import java.util.UUID;

public class Container {
	private final String uuid;
	private final String fileName;

	private final File tempFolder;
	private File tempFolderChunked;

	private int currentPart;
	private int totalPart;

	public Container(File tempFolder, String fileName) {
		if (!tempFolder.exists()) tempFolder.mkdirs();
		this.tempFolder = tempFolder;
		this.fileName = fileName;
		this.uuid = UUID.randomUUID().toString();
	}

	public String getUuid() {
		return uuid;
	}

	public String getFileName() {
		return fileName;
	}

	public File getTempFolder() {
		return tempFolder;
	}


	public File getTempFolderChunked() {
		if (!tempFolderChunked.exists()) {
			System.out.println("Chunked folder does not exist, creating...");
			tempFolderChunked = new File(tempFolder + "/chunked");
			tempFolderChunked.mkdirs();
		} else {
			System.out.println("Chunked folder already exists");
		}
		return tempFolderChunked;
	}

	public void setTempFolderChunked(File tempFolderChunked) {
		this.tempFolderChunked = tempFolderChunked;
	}

	public int getCurrentPart() {
		return currentPart;
	}

	public void setCurrentPart(int currentPart) {
		this.currentPart = currentPart;
	}

	public int getTotalPart() {
		return totalPart;
	}

	public void setTotalPart(int totalPart) {
		this.totalPart = totalPart;
	}
}
