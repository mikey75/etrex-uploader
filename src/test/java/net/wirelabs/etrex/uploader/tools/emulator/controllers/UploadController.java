package net.wirelabs.etrex.uploader.tools.emulator.controllers;

import com.strava.model.DetailedActivity;
import com.strava.model.Upload;
import net.wirelabs.etrex.uploader.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class UploadController  {
    @Autowired
    private FileReader fileReader;
    // this is not a strict strava functionality - we do not create files - the upload has fixed id, and serves fixed response
    // this is enough for client test, we don't want to emulate what strava does with uploads.
    @PostMapping("/uploads")
    public ResponseEntity<?> upload() {
        String json = fileReader.readFileContents("src/test/resources/strava-emulator/uploads/upload.json"); // this upload has id 9999999
        if (!json.isEmpty()) {
            return new ResponseEntity<>(JsonUtil.deserialize(json, DetailedActivity.class), HttpStatus.OK);
        }
        return ResponseEntity.internalServerError().build();
    }

    @GetMapping("/uploads/{id}")
    public ResponseEntity<?> getUpload(@PathVariable("id") String id) {
        String json = fileReader.readFileContents("src/test/resources/strava-emulator/uploads/" + id + "/upload.json");
        if (!json.isEmpty()) {
            return new ResponseEntity<>(JsonUtil.deserialize(json, Upload.class), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }
}
