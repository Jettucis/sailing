package com.duckblade.osrs.sailing.features.barracudatrials.splits;

import com.duckblade.osrs.sailing.features.barracudatrials.BarracudaDifficulty;
import com.duckblade.osrs.sailing.features.barracudatrials.BarracudaTrial;
import java.util.List;
import lombok.Value;

@Value
public class BarracudaSplitsComplete
{

	BarracudaTrial trial;
	BarracudaDifficulty difficulty;
	int kc;
	List<BarracudaSplit> splits;

}
