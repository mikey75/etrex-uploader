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
        throw new IllegalStateException("Unsupported operating system");
    }


    // windows automounts usb drives as new drives
    List<File> windowsRoots() {
        File[] roots = File.listRoots();
        if (roots != null) {
            return Arrays.asList(File.listRoots());
        } else {
            return Collections.emptyList();
        }
    }

    // linux automounts usb drives in /media/$user
    List<File> linuxRoots() {
        String user = System.getProperty("user.name");
        File root = new File(Constants.LINUX_USB_MOUNTDIR, user);
        File[] list = root.listFiles();

        if (list != null) {
            return Arrays.asList(list);
        } else {
            return Collections.emptyList();
        }

    }

    List<File> osxRoots() {
        File root = new File(Constants.OSX_USB_MOUNTDIR);
        File[] list = root.listFiles();

        if (list != null) {
            return Arrays.asList(list);
        } else {
            return Collections.emptyList();
        }

    }

}
