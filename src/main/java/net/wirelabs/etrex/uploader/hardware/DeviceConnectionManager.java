package net.wirelabs.etrex.uploader.hardware;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class DeviceConnectionManager  {
    
    private DriveObserver observer;
    
    public void startObserverThread() {
        observer = new DriveObserver(); 
        new Thread(observer).start();    
    }
    
        
        
        
   
}
    