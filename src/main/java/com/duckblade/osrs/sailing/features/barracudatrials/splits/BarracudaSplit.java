package com.duckblade.osrs.sailing.features.barracudatrials.splits;

import lombok.Value;
import net.runelite.api.Constants;

@Value
public class BarracudaSplit
{
	String name;
	int ticks;

	String getFormattedTicks()
	{
		int ms = ticks * Constants.GAME_TICK_LENGTH;

		int cents = (ms / 10);
		int seconds = (ms / 1_000);
		int minutes = seconds / 60;
		return String.format("%d:%02d.%02d",
			minutes,
			seconds % 60,
			cents % 100
		);
	}
}
