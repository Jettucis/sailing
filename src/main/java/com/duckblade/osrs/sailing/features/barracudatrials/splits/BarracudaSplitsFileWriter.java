package com.duckblade.osrs.sailing.features.barracudatrials.splits;

import com.duckblade.osrs.sailing.SailingConfig;
import com.duckblade.osrs.sailing.SailingPlugin;
import com.duckblade.osrs.sailing.module.PluginLifecycleComponent;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.ColorUtil;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BarracudaSplitsFileWriter implements PluginLifecycleComponent
{

	private static final File SPLITS_DIR = new File(SailingPlugin.SAILING_DIR, "splits");

	private final ChatMessageManager chatMessageManager;

	private SailingConfig.BarracudaSplitsToFileMode mode;

	@Override
	public boolean isEnabled(SailingConfig config)
	{
		mode = config.barracudaSplitsToFile();
		return mode != SailingConfig.BarracudaSplitsToFileMode.OFF;
	}

	@Subscribe
	public void onBarracudaSplitsComplete(BarracudaSplitsComplete complete)
	{
		// Ensure base directory exists
		if (!SPLITS_DIR.exists() && !SPLITS_DIR.mkdirs())
		{
			log.warn("Unable to create sailing directory at {}", SPLITS_DIR.getAbsolutePath());
			return;
		}

		String filename = String.format("%s - %s - %d.txt",
			complete.getTrial(),
			complete.getDifficulty(),
			complete.getKc());

		Path path = SPLITS_DIR.toPath().resolve(filename);
		try
		{
			writeSplits(path, complete.getSplits());

			if (mode == SailingConfig.BarracudaSplitsToFileMode.NOTIFY)
			{
				chatMessageManager.queue(QueuedMessage.builder()
					.type(ChatMessageType.GAMEMESSAGE)
					.sender("Runelite/Sailing")
					.value("[Sailing] Barracuda splits written to " + ColorUtil.wrapWithColorTag(path.toAbsolutePath().toString(), Color.RED))
					.build());
			}
		}
		catch (IOException ex)
		{
			log.warn("Failed writing barracuda splits to {}", path, ex);
		}
	}

	private static void writeSplits(Path file, List<BarracudaSplit> splits) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		for (BarracudaSplit s : splits)
		{
			sb.append(s.getName())
				.append(": ")
				.append(s.getFormattedTicks())
				.append(System.lineSeparator());
		}

		Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
	}
}
