package net.wirelabs.etrex.uploader.device;

import net.wirelabs.etrex.uploader.common.Constants;
import net.wirelabs.etrex.uploader.common.utils.SystemUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    // windows automounts usb drives as new drives
    List<File> windowsRoots() {
        File[] roots = File.listRoots();
        return createFileList(roots);
    }

    // linux automounts usb drives in /media/$user (specified in Constants.LINUX_USB_MOUNTDIR)
    List<File> linuxRoots() {
        String user = System.getProperty("user.name");
        File root = new File(Constants.LINUX_USB_MOUNT_DIR, user);
        File[] list = root.listFiles();
        return createFileList(list);
    }

    // osx automounts usb drives in /Volumes (specified in Constants.OSX_USB_MOUNTDIR)
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
