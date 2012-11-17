/**
 * 
 */
package org.mumod.android;

import java.util.ArrayList;

import org.mumod.geonames.GeoName;
import org.mumod.statusnet.DirectMessage;

import android.content.Context;

/**
 * @author macno
 *
 */
public class MessagingListener {

    public void loadRemoteImageStarted(Context context) {
    }
    
    public void loadRemoteImageFinished(Context context) {
    }

    public void loadRemoteImageFailed(Context context, String reason) {
    }
    
    public void loadRemoteTextStarted(Context context) {
    }
    
    public void loadRemoteTextFinished(Context context, String text ) {
    }

    public void loadRemoteTextFailed(Context context, String reason) {
    }
    
    public void loadGeonameStarted(Context context) {
    }
    
    public void loadGeonameFinished(Context context, GeoName geoname ) {
    }

    public void loadGeonameFailed(Context context, String reason) {
    }
    
    public void loadDirectMessagesStarted() {
    }
    
    public void loadDirectMessagesFinished(ArrayList<DirectMessage> dms, int versus) {
    }

    public void loadDirectMessagesFailed(String reason) {
    }
    
    public void sendDirectMessageStarted() {
    }
    
    public void sendDirectMessageFinished() {
    }

    public void sendDirectMessageFailed(String reason) {
    }
}
