package net.wirelabs.etrex.uploader.tools.emulator.controllers;

import com.strava.model.DetailedActivity;
import com.strava.model.StreamSet;
import com.strava.model.UpdatableActivity;
import net.wirelabs.etrex.uploader.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ActivitiesController  {
    @Autowired
    private FileReader fileReader;

    @GetMapping("/activities/{id}")
    public ResponseEntity<?> getActivityById(@PathVariable("id") String id) {
        String json = fileReader.readFileContents("src/test/resources/strava-emulator/activities/" + id + "/activity.json");
        if (!json.isEmpty()) {
            return new ResponseEntity<>(JsonUtil.deserialize(json, DetailedActivity.class), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No activity with id=" + id);

    }

    @GetMapping("/activities/{id}/streams")
    public StreamSet getActivityStreams(@PathVariable("id") String id, @RequestParam("keys") String keys, @RequestParam("key_by_type") String keysByType) {
        String json = fileReader.readFileContents("src/test/resources/strava-emulator/activities/" + id + "/activity-stream.json");
        return JsonUtil.deserialize(json, StreamSet.class);
    }

    @PutMapping("/activities/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable("id") String id, @RequestBody String body) {
        UpdatableActivity activity = JsonUtil.deserialize(body, UpdatableActivity.class);
        String activityFile = fileReader.readFileContents("src/test/resources/strava-emulator/activities/" + id + "/activity.json");

        DetailedActivity updatedOriginalActivity = JsonUtil.deserialize(activityFile, DetailedActivity.class);
        updatedOriginalActivity.setCommute(activity.isCommute());
        updatedOriginalActivity.setName(activity.getName());
        updatedOriginalActivity.setSportType(activity.getSportType());
        updatedOriginalActivity.setDescription(activity.getDescription());

        return new ResponseEntity<>(JsonUtil.serialize(updatedOriginalActivity), HttpStatus.OK);
    }

}