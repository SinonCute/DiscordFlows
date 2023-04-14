package live.karyl.task;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.EventListener;
import java.util.EventObject;

public class FileWatcher implements Runnable {
	private final WatchService watchService;
	private final ExecutorService executorService;
	private final Thread listenerThread;
	private final FileProcessedEventListener listener;

	public FileWatcher(Path directory, FileProcessedEventListener listener) throws Exception {
		// create a watch service on the directory
		watchService = directory.getFileSystem().newWatchService();
		directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

		// create a thread pool with 5 worker threads
		executorService = Executors.newFixedThreadPool(5);

		// create a listener thread
		listenerThread = new Thread(this);

		// set the listener
		this.listener = listener;

		start();
	}

	public void start() {
		// start the listener thread
		listenerThread.start();
	}

	public void stop() throws Exception {
		// stop the listener thread, shutdown the thread pool, and close the watch service
		watchService.close();
		executorService.shutdown();
		listenerThread.interrupt();
		listenerThread.join();
	}

	@Override
	public void run() {
		while (true) {
			try {
				// wait for the events
				WatchKey watchKey = watchService.take();

				// process each event
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();

					// handle the create event
					if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
						Path path = (Path) event.context();
						String filename = path.toString();
						File file = new File(filename);

						if (filename.endsWith(".tmp")) {
							continue;
						}

						// submit the file processing task to the thread pool
						executorService.submit(() -> listener.newFileFound(new FileEvent(file)));
					}
				}

				// reset the watch key
				boolean valid = watchKey.reset();
				if (!valid) {
					// watch key is no longer valid, exit the loop
					break;
				}
			} catch (InterruptedException e) {
				// thread interrupted, exit the loop
				break;
			}
		}
	}

	public interface FileProcessedEventListener extends EventListener {
		void newFileFound(FileEvent event);
	}

	public static class FileEvent extends EventObject {
		public FileEvent(File file) {
			super(file);
		}

		public File getFile() {
			return (File) super.getSource();
		}
	}
}
