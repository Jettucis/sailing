package com.duckblade.osrs.sailing.features.barracudatrials;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.VarPlayerID;

@Getter
@RequiredArgsConstructor
public enum BarracudaTrial
{

	TEMPOR_TANTRUM(
		"Tempor Tantrum",
		new WorldArea(2944, 2751, 3136 - 2944, 2943 - 2751, 0),
		VarPlayerID.SAILING_BT_TRIAL_TEMPOR_TANTRUM_COMPLETED,
		new int[]{1, 2, 3}
	),
	JUBBLY_JIVE(
		"Jubbly Jive",
		new WorldArea(2210, 2880, 2488 - 2210, 3072 - 2880, 0),
		VarPlayerID.SAILING_BT_TRIAL_JUBBLY_JIVE_COMPLETED,
		new int[]{1, 2, 3}
	),

	// this one's worldarea is not very "snug" and might need trimmed down to a compound area or list of region ids or something
	GWENITH_GLIDE(
		"Gwenith Glide",
		new WorldArea(2026, 3071, 2295 - 2026, 3653 - 3071, 0),
		VarPlayerID.SAILING_BT_TRIAL_GWENITH_GLIDE_COMPLETED,
		new int[]{3, 5, 8}
	),
	;

	private final String name;
	private final WorldArea area;
	private final int kcVarp;
	private final int[] difficultyProgressSteps;

	public int getKc(Client client)
	{
		return client.getVarpValue(kcVarp);
	}

	public int getProgressSteps(BarracudaDifficulty difficulty)
	{
		return difficultyProgressSteps[difficulty.ordinal()];
	}

	public static BarracudaTrial byLocation(WorldPoint wp)
	{
		for (BarracudaTrial trial : BarracudaTrial.values())
		{
			if (trial.getArea().contains(wp))
			{
				return trial;
			}
		}

		return null;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
