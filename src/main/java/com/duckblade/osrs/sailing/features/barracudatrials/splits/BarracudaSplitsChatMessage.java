package com.duckblade.osrs.sailing.features.barracudatrials.splits;

import com.duckblade.osrs.sailing.SailingConfig;
import com.duckblade.osrs.sailing.module.PluginLifecycleComponent;
import java.awt.Color;
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
public class BarracudaSplitsChatMessage
	implements PluginLifecycleComponent
{

	private final ChatMessageManager chatMessageManager;

	@Override
	public boolean isEnabled(SailingConfig config)
	{
		return config.barracudaSplitsChatMessage();
	}

	@Subscribe
	public void onBarracudaSplit(BarracudaSplit split)
	{
		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.GAMEMESSAGE)
			.sender("Runelite/Sailing")
			.value("[Sailing] " + split.getName() + ": " + ColorUtil.wrapWithColorTag(split.getFormattedTicks(), Color.RED))
			.build());
	}

	@Subscribe
	public void onBarracudaSplitsComplete(BarracudaSplitsComplete complete)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[Sailing] Splits:<br>");

		for (BarracudaSplit s : complete.getSplits())
		{
			sb.append(s.getName())
				.append(": ")
				.append(ColorUtil.wrapWithColorTag(s.getFormattedTicks(), Color.RED))
				.append("<br>");
		}

		chatMessageManager.queue(QueuedMessage.builder()
			.type(ChatMessageType.GAMEMESSAGE)
			.sender("Runelite/Sailing")
			.value(sb.toString())
			.build());
	}

}
