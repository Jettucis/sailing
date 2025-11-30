package com.duckblade.osrs.sailing.features.barracudatrials.splits;

import com.duckblade.osrs.sailing.SailingConfig;
import com.duckblade.osrs.sailing.SailingPlugin;
import com.duckblade.osrs.sailing.features.barracudatrials.BarracudaDifficulty;
import com.duckblade.osrs.sailing.features.barracudatrials.BarracudaTrial;
import com.duckblade.osrs.sailing.module.PluginLifecycleComponent;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

@Slf4j
@Singleton
public class BarracudaSplitsOverlayPanel
	extends OverlayPanel
	implements PluginLifecycleComponent
{

	// how long to keep rendering the overlay after you've completed a run
	private static final int RENDER_DECAY_MS = 60 * 1_000;

	private final BarracudaSplitsTracker tracker;

	private long runEndTs;

	@Inject
	public BarracudaSplitsOverlayPanel(SailingPlugin plugin, BarracudaSplitsTracker tracker)
	{
		super(plugin);

		this.tracker = tracker;
		setPreferredPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ALWAYS_ON_TOP);
	}

	@Override
	public boolean isEnabled(SailingConfig config)
	{
		return config.barracudaSplitsOverlayPanel();
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!shouldRender())
		{
			return null;
		}

		BarracudaTrial trial = tracker.getTrial();
		BarracudaDifficulty difficulty = tracker.getDifficulty();
		if (trial == null || difficulty == null)
		{
			return null;
		}

		int steps = trial.getProgressSteps(difficulty);
		String splitWord = tracker.getSplitWord();

		// Title
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(trial.name().replace('_', ' '))
			.build());

		for (int i = 1; i <= steps; i++)
		{
			String right = "--:--.--";
			if (i <= tracker.getSplits().size())
			{
				right = tracker.getSplits().get(i - 1).getFormattedTicks();
			}

			panelComponent.getChildren().add(LineComponent.builder()
				.left(splitWord + " " + i)
				.right(right)
				.build());
		}

		return super.render(graphics);
	}

	private boolean shouldRender()
	{
		if (tracker.isInRun())
		{
			runEndTs = -1;
			return true;
		}

		if (runEndTs == -1)
		{
			runEndTs = System.currentTimeMillis() + RENDER_DECAY_MS;
			return true;
		}

		return System.currentTimeMillis() <= runEndTs;
	}
}
