package net.wirelabs.etrex.uploader.gui.desktop.stravapanel.detailsdialog;

import com.strava.model.PhotosSummaryPrimary;
import net.wirelabs.etrex.uploader.gui.common.base.BasePanel;
import net.wirelabs.etrex.uploader.strava.StravaException;
import net.wirelabs.etrex.uploader.strava.client.StravaClient;
import net.wirelabs.etrex.uploader.utils.SwingUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

import static net.wirelabs.etrex.uploader.gui.desktop.stravapanel.activitiestable.StravaActivitiesPanel.*;


public class PhotosPanel extends BasePanel {

    private final JLabel statusLabel = new JLabel("Loading photos");
    private final JPanel contentScrollPanel = new BasePanel("insets 0", "[]", "[]");
    private final JScrollPane scrollPane = new JScrollPane(contentScrollPanel);

    public PhotosPanel() {
        super("Photos");
        add(scrollPane, "cell 0 0, growx, alignx left, growy, aligny top");
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        setVisible(true);

    }

    public void getPhotos(StravaClient stravaClient, Long id, int size) {

        contentScrollPanel.add(statusLabel);
        SwingUtilities.invokeLater(() -> {
            try {

                List<PhotosSummaryPrimary> photos = getAndCachePhotoUrls(stravaClient, id, String.valueOf(size));

                if (photos.isEmpty()) {
                    statusLabel.setText("No photos");
                    statusLabel.setForeground(Color.RED);
                    return;
                }

                List<Map<String, String>> m = photos.stream().map(PhotosSummaryPrimary::getUrls).toList();
                List<String> urls = m.stream().map(x -> x.get(String.valueOf(size))).toList();

                int i = 0;
                for (String url : urls) {
                    BufferedImage img = getAndCacheImageFromUrl(url);
                    JLabel imageLabel = new JLabel();
                    imageLabel.setIcon(new ImageIcon(img));
                    // 4 images in a row
                    contentScrollPanel.add(imageLabel, "cell " + i % 4 + " " + i++ / 4);

                }

                contentScrollPanel.remove(statusLabel);
                revalidate();
                repaint();
            } catch (IOException | StravaException e) {
                SwingUtils.errorMsg(e.getMessage());
            }

        });
    }

    private static BufferedImage getAndCacheImageFromUrl(String url) throws IOException {
        BufferedImage img;
        // if image with this url is not already cached - get it and cache it
        if (!getPhotoCache().containsKey(url)) {
            // since images are on cloudfront - don't need strava credentials, just get the images on urls
            img = ImageIO.read(new URL(url));
            // cache the image
            getPhotoCache().put(url, img);
        } else {
            // it is cached, get it from cache
            img = getPhotoCache().get(url);
        }
        return img;
    }

    private List<PhotosSummaryPrimary> getAndCachePhotoUrls(StravaClient stravaClient, long activityId, String size) throws StravaException {
        String url = stravaClient.getActivitiesUrl() + "/" + activityId +"/photos" + "?size=" + size;
        if (!getPhotoUrlsCache().containsKey(url)) {
            PhotosSummaryPrimary[] result = stravaClient.makeGetRequest(url, PhotosSummaryPrimary[].class, null);
            getPhotoUrlsCache().put(url, Arrays.stream(result).toList());
            return Arrays.stream(result).toList();
        } else {
            return getPhotoUrlsCache().get(url);
        }
    }
}
