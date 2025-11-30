package com.duckblade.osrs.sailing.features.barracudatrials.splits;

import com.duckblade.osrs.sailing.features.barracudatrials.BarracudaDifficulty;
import com.duckblade.osrs.sailing.features.barracudatrials.BarracudaTrial;
import com.duckblade.osrs.sailing.features.util.SailingUtil;
import com.duckblade.osrs.sailing.module.PluginLifecycleComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarPlayerID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
@Singleton
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class BarracudaSplitsTracker
	implements PluginLifecycleComponent
{

	private final Client client;
	private final EventBus eventBus;

	@Getter
	private final List<BarracudaSplit> splits = new ArrayList<>();

	@Getter
	private boolean inRun;
	private int kc; // we use the varp change event to determine if the run was fully completed
	private int btStartServer;
	private int btStartClient;
	private int progress;

	private boolean trialStart;
	private boolean trialEnd;

	@Getter
	private BarracudaTrial trial;

	@Getter
	private BarracudaDifficulty difficulty;

	@Override
	public void shutDown()
	{
		reset();
	}

	@Subscribe
	public void onGameTick(GameTick e)
	{
		if (!SailingUtil.isSailing(client))
		{
			return;
		}

		if (trialStart)
		{
			reset();
			splits.clear();
			inRun = true;

			trial = BarracudaTrial.byLocation(SailingUtil.getTopLevelWorldPoint(client));
			difficulty = BarracudaDifficulty.current(client);
			if (trial == null || difficulty == null)
			{
				log.warn("trial and/or difficulty not found, did they add new features? t={} d={}", trial, difficulty);
				return;
			}

			kc = trial.getKc(client);
			log.debug("player began {}/{}, kc={}", trial, difficulty, kc);

			// it's impossible to begin moving on the tick where trialStart is set so this return is safe
			return;
		}

		if (trialEnd)
		{
			if (trial.getKc(client) <= kc)
			{
				log.debug("player abandoned in-progress barracuda trial");
				reset();
				return;
			}

			BarracudaSplit split = new BarracudaSplit(getSplitWord() + " " + progress, client.getTickCount() - btStartClient); // add in the final progress step
			splits.add(split);

			log.debug("bt run complete: {}", splits);
			eventBus.post(new BarracudaSplitsComplete(
				trial,
				difficulty,
				kc,
				Collections.unmodifiableList(splits))
			);

			reset();
			return;
		}

		if (!inRun)
		{
			return;
		}

		if (btStartServer != (btStartServer = client.getVarpValue(VarPlayerID.SAILING_BT_TIME_START)) &&
			btStartServer != 0)
		{
			btStartClient = client.getTickCount();
			log.debug("player began run of {}/{} on s={} c={}", trial, difficulty, btStartServer, btStartClient);
			return;
		}

		if (inRun &&
			progress != (progress = getProgress()) &&
			progress < trial.getProgressSteps(difficulty)) // ignore the last one so we get a "final" time only
		{
			BarracudaSplit split = new BarracudaSplit(getSplitWord() + " " + progress, client.getTickCount() - btStartClient);
			splits.add(split);
			eventBus.post(split);
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged e)
	{
		if (e.getVarbitId() == VarbitID.SAILING_BT_IN_TRIAL)
		{
			if (e.getValue() != 0)
			{
				log.trace("trialStart");
				trialStart = true;
			}
			else
			{
				log.trace("trialEnd");
				trialEnd = true;
			}
		}
	}

	private void reset()
	{
		// intentionally does not unset trial, difficulty, and splits.clear()
		// to allow overlay panel to keep showing after the run
		inRun = false;
		btStartServer = 0;
		kc = 0;
		progress = 0;
		trialStart = false;
		trialEnd = false;
	}

	private int getProgress()
	{
		Widget widget = client.getWidget(InterfaceID.SailingBtHud.BT_TRACKER_PROGRESS);
		return widget != null ? Integer.parseInt(widget.getText().split("/")[0].trim()) : 0;
	}

	String getSplitWord()
	{
		switch (trial)
		{
			case JUBBLY_JIVE:
				return "Jubbly";

			case GWENITH_GLIDE:
				return "Crystal";

			default:
				return "Lap";
		}
	}
}
