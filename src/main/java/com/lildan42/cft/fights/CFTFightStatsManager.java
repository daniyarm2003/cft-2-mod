package com.lildan42.cft.fights;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lildan42.cft.CFT2Mod;
import com.lildan42.cft.fighterdata.fighters.Fighter;
import com.lildan42.cft.fighterdata.fighters.FighterSkill;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class CFTFightStatsManager implements CFTFightStatsRecorder {

    private final Map<Integer, StatsEntry> statsEntries = new HashMap<>();

    private void reportResult(Fighter fighter, boolean isWin) {
        StatsEntry entry = this.statsEntries.computeIfAbsent(fighter.getId(), id -> StatsEntry.fromFighter(fighter));

        int wins = isWin ? entry.wins() + 1 : entry.wins();
        int losses = isWin ? entry.losses() : entry.losses() + 1;

        this.statsEntries.put(fighter.getId(), new StatsEntry(entry.id(), entry.fighterStats(), wins, losses));
    }

    @Override
    public void reportWin(Fighter winner) {
        this.reportResult(winner, true);
    }

    @Override
    public void reportLoss(Fighter loser) {
        this.reportResult(loser, false);
    }

    public void exportStats(StatsEntryExporter exporter) {
        List<StatsEntry> entries = List.copyOf(this.statsEntries.values());

        try {
            exporter.export(entries);
        }
        catch (IOException e) {
            CFT2Mod.LOGGER.error("Unable to export fight stats due to an I/O error", e);
        }
    }

    public record StatsEntry(int id, Map<String, Double> fighterStats, int wins, int losses) {
        public static StatsEntry fromFighter(Fighter fighter) {
            Map<String, Double> stats = Arrays.stream(FighterSkill.SkillType.values())
                    .collect(Collectors.toMap(FighterSkill.SkillType::getName, fighter::getSkillLevel));

            return new StatsEntry(fighter.getId(), stats, 0, 0);
        }
    }

    public interface StatsEntryExporter {
        void export(List<StatsEntry> stats) throws IOException;

        static StatsEntryExporter json(File file) {
            return new JsonStatsEntryExporter(file, CFT2Mod.DEFAULT_JSON_MAPPER);
        }

        static StatsEntryExporter csv(File file) {
            return new CsvStatsEntryExporter(file);
        }
    }

    private record JsonStatsEntryExporter(File outputFile, ObjectMapper jsonMapper) implements StatsEntryExporter {
        @Override
        public void export(List<StatsEntry> stats) throws IOException {
            try (FileOutputStream writeStream = new FileOutputStream(this.outputFile)) {
                this.jsonMapper.writeValue(writeStream, stats);
            }

            CFT2Mod.LOGGER.info("Recorded JSON fight stats to {}", this.outputFile.getAbsolutePath());
        }
    }

    private record CsvStatsEntryExporter(File outputFile) implements StatsEntryExporter {
        @Override
        public void export(List<StatsEntry> stats) throws IOException {
            try(FileOutputStream writeStream = new FileOutputStream(this.outputFile)) {
                List<String> rows = new ArrayList<>();
                List<String> header = new ArrayList<>(List.of("id", "wins", "losses"));

                for(FighterSkill.SkillType skillType : FighterSkill.SkillType.values()) {
                    String colName = skillType.getName().toLowerCase(Locale.ROOT).replace(' ', '_');
                    header.add(colName);
                }

                rows.add(String.join(",", header));

                for(StatsEntry entry : stats) {
                    List<String> cols = new ArrayList<>();

                    cols.add(String.valueOf(entry.id()));
                    cols.add(String.valueOf(entry.wins()));
                    cols.add(String.valueOf(entry.losses()));

                    entry.fighterStats().values()
                            .stream().map(String::valueOf)
                            .forEach(cols::add);

                    rows.add(String.join(",", cols));
                }

                String output = String.join("\n", rows);
                byte[] outputBytes = output.getBytes(StandardCharsets.UTF_8);

                writeStream.write(outputBytes);
            }

            CFT2Mod.LOGGER.info("Recorded CSV fight stats to {}", this.outputFile.getAbsolutePath());
        }
    }
}
