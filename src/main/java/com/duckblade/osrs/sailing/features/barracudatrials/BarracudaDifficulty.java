package com.duckblade.osrs.sailing.features.barracudatrials;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.gameval.VarbitID;

@Getter
@RequiredArgsConstructor
public enum BarracudaDifficulty
{

	SWORDFISH("Swordfish", 2),
	SHARK("Shark", 3),
	MARLIN("Marlin", 4),
	;

	private final String name;
	private final int inTrialVarbValue;

	public static BarracudaDifficulty ofVarbitValue(int varbitValue)
	{
		for (BarracudaDifficulty difficulty : values())
		{
			if (difficulty.getInTrialVarbValue() == varbitValue)
			{
				return difficulty;
			}
		}
		return null;
	}

	public static BarracudaDifficulty current(Client client)
	{
		int varbitValue = client.getVarbitValue(VarbitID.SAILING_BT_IN_TRIAL);
		if (varbitValue == 1)
		{
			// unranked (1) is always the same as swordfish (2)
			varbitValue = 2;
		}

		return ofVarbitValue(varbitValue);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
