package com.lildan42.cft.fights;

import com.lildan42.cft.fighterdata.fighters.Fighter;

public interface CFTFightStatsRecorder {
    void reportWin(Fighter winner);
    void reportLoss(Fighter loser);
}
