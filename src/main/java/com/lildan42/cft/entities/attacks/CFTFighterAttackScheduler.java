package com.lildan42.cft.entities.attacks;

import java.util.ArrayList;
import java.util.List;

public class CFTFighterAttackScheduler {
    private final List<ScheduledAttack> attacks = new ArrayList<>();

    public void scheduleAttack(Runnable attack, int delayTicks) {
        this.attacks.add(new ScheduledAttack(attack, delayTicks));
    }

    public void update() {
        this.attacks.forEach(ScheduledAttack::update);
        this.attacks.removeIf(ScheduledAttack::isDelayFinished);
    }

    private static class ScheduledAttack {
        private final Runnable attack;
        private int delayTicks;

        public ScheduledAttack(Runnable attack, int delayTicks) {
            this.attack = attack;
            this.delayTicks = delayTicks;
        }

        public boolean isDelayFinished() {
            return this.delayTicks <= 0;
        }

        public void update() {
            this.delayTicks--;

            if(this.isDelayFinished()) {
                this.attack.run();
            }
        }
    }
}
