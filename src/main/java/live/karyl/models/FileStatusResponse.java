package live.karyl.models;

public class FileStatusResponse {
	private String fileName;
	private int currentPart;
	private int totalParts;

	public FileStatusResponse(String fileName, int currentPart, int totalParts) {
		this.fileName = fileName;
		this.currentPart = currentPart;
		this.totalParts = totalParts;
	}

	public String getFileName() {
		return fileName;
	}

	public int getCurrentPart() {
		return currentPart;
	}

	public int getTotalParts() {
		return totalParts;
	}
}
