package live.karyl.models;

public record UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size){}
