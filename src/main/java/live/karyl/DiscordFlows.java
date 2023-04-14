package live.karyl;

import live.karyl.listener.FileListener;
import live.karyl.task.FileWatcher;

import java.nio.file.Path;

public class DiscordFlows {
	public static void main(String[] args) throws Exception {
		System.out.println("Hello World!");
		new FileWatcher(Path.of("C:\\Users\\caoth\\Desktop\\t"), new FileListener());

	}
}
