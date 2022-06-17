package net.wirelabs.etrex.uploader;

import net.wirelabs.etrex.uploader.hardware.DeviceConnectionManager;

public class ApplicationStarter {

    public static void main(String[] args) {

        DeviceConnectionManager connectionManager = new DeviceConnectionManager();
        connectionManager.startObserverThread();
        //connectionManager.getObserver().stop();
        
        
        
    
    }
    
}
