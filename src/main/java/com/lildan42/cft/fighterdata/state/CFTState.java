package com.lildan42.cft.fighterdata.state;

import com.lildan42.cft.fighterdata.fighters.Fighter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CFTState {
    private List<Fighter> fighters;
    private final Saver stateSaver;

    public CFTState(Saver stateSaver) {
        this.fighters = new ArrayList<>();
        this.stateSaver = stateSaver;
    }

    public void loadState() throws IOException {
        if(this.stateSaver.isSaved()) {
            SaveContext saveContext = this.stateSaver.load();
            this.fighters = new ArrayList<>(saveContext.fighters());
        }
    }

    public void saveState() throws IOException {
        this.stateSaver.save(this.getSaveContext());
    }

    public List<Fighter> getActiveFighters() {
        return this.fighters.stream().filter(fighter -> !fighter.isDeleted()).toList();
    }

    private SaveContext getSaveContext() {
        return new SaveContext(List.copyOf(this.fighters));
    }

    public record SaveContext(List<Fighter> fighters) {}

    public interface Saver {
        void save(SaveContext context) throws IOException;
        boolean isSaved();
        SaveContext load() throws IOException;
    }
}