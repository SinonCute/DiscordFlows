package live.karyl.listener;

import live.karyl.task.FileWatcher;

public class FileListener implements FileWatcher.FileProcessedEventListener {

	@Override
	public void newFileFound(FileWatcher.FileEvent event) {
		System.out.println("New file found: " + event.getFile().getName());
	}
}
