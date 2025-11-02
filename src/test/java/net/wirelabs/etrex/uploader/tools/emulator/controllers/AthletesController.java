package net.wirelabs.etrex.uploader.tools.emulator.controllers;

import com.strava.model.ActivityStats;
import com.strava.model.DetailedAthlete;
import com.strava.model.SummaryActivity;
import lombok.extern.slf4j.Slf4j;
import net.wirelabs.etrex.uploader.utils.JsonUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
public class AthletesController {
    @Autowired
    private FileReader fileReader;
    public String currentAthlete = "123456789";

    @GetMapping("/athlete")
    public ResponseEntity<?> getLoggedInAthlete() {

        String json = fileReader.readFileContents("src/test/resources/strava-emulator/athletes/" + currentAthlete + "/athlete.json");
        if (!json.isEmpty()) {
            return ResponseEntity.of(Optional.ofNullable(JsonUtil.deserialize(json, DetailedAthlete.class)));
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/athlete/activities")
    public ResponseEntity<List<SummaryActivity>> getAthleteActivities() {

        File dir = new File("src/test/resources/strava-emulator/athletes/" + currentAthlete + "/activities");

        if (dir.exists() && dir.isDirectory()) {
            List<File> files = FileUtils.listFiles(dir, new String[]{"json"}, false).stream().filter(f -> f.getName().contains("activity")).toList();
            List<SummaryActivity> activities = new ArrayList<>();
            for (File f : files) {
                String fs = fileReader.readFileContents(f.getPath());
                activities.add(JsonUtil.deserialize(fs, SummaryActivity.class));
            }
            return ResponseEntity.of(Optional.of(activities));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/athletes/{id}/stats")
    public ResponseEntity<ActivityStats> getActivityStats(@PathVariable("id") String id) {

        String json = fileReader.readFileContents("src/test/resources/strava-emulator/athletes/" + id + "/stats/stats.json");
        if (!json.isEmpty()) {
            return ResponseEntity.of(Optional.ofNullable(JsonUtil.deserialize(json, ActivityStats.class)));
        }
        return ResponseEntity.notFound().build();

    }


}
