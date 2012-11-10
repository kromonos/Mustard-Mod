/**
 * 
 */
package org.mustard.android.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author macno
 *
 */
public class Mustard extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MustardMain.actionHandleTimeline(this);
        finish();
    }

}
