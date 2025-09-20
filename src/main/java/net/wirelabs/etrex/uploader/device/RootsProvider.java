package net.wirelabs.etrex.uploader.device;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.utils.SystemUtils;

import java.io.File;
import java.util.*;

/**
 * Created 8/5/22 by Michał Szwaczko (mikey@wirelabs.net)
 */
public class RootsProvider {

    public List<File> getRoots() {

        if (SystemUtils.isWindows()) {
            return windowsRoots();
        }
        if (SystemUtils.isLinux()) {
            return linuxRoots();
        }
        if (SystemUtils.isOSX()) {
            return osxRoots();
        }
        return Collections.emptyList();
    }

    // windows auto-mounts usb drives as new drives
    List<File> windowsRoots() {
        File[] roots = File.listRoots();
        return createFileList(roots);
    }

    // linux auto-mounts usb drives in /media/$user (specified in Constants.LINUX_USB_MOUNT_DIR)
    // or /run/media/$user (Constants.LINUX_ALT_USB_MOUNT_DIR)
    List<File> linuxRoots() {
        String user = System.getProperty("user.name");
        File root = new File(Constants.LINUX_USB_MOUNT_DIR, user);
        File altRoot = new File(Constants.LINUX_ALT_USB_MOUNT_DIR, user);

        List<File> rootDirs = List.of(root, altRoot);
        // in perfect world - you either have /media/$user or /run/media/$user - can't have both
        // but for the safety of some unknown/badly configured distributions we scan both and return
        List<File> roots = new ArrayList<>();
        for (File dir : rootDirs) {
            if (dir.isDirectory()) {
                roots.addAll(createFileList(dir.listFiles()));
            }
        }

        return roots;
    }

    // osx auto-mounts usb drives in /Volumes (specified in Constants.OSX_USB_MOUNT_DIR)
    List<File> osxRoots() {
        File root = new File(Constants.OSX_USB_MOUNT_DIR);
        File[] list = root.listFiles();
        return createFileList(list);
    }

    private List<File> createFileList(File[] list) {
        if (list != null) {
            return Arrays.asList(list);
        } else {
            return Collections.emptyList();
        }
    }
}
