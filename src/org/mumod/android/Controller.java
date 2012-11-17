/**
 * 
 */
package org.mumod.android;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.mumod.android.provider.GeoNames;
import org.mumod.android.provider.StatusNet;
import org.mumod.geonames.GeoName;
import org.mumod.statusnet.DirectMessage;
import org.mumod.util.HttpManager;

import android.content.Context;
import android.os.Process;
import android.util.Config;
import android.util.Log;

/**
 * @author macno
 *
 */
public class Controller implements Runnable {

	private static final String TAG = "Mustard/MessagingController";

	private static Controller inst = null;
	private BlockingQueue<Command> mCommands = new LinkedBlockingQueue<Command>();

	private boolean mBusy;
	private Thread mThread;

	private HashSet<MessagingListener> mListeners = new HashSet<MessagingListener>();

	protected Controller(Context _context) {
		mThread = new Thread(this);
		mThread.start();
	}

	/**
	 * Gets or creates the singleton instance of MessagingController. Application is used to
	 * provide a Context to classes that need it.
	 * @param application
	 * @return
	 */
	public synchronized static Controller getInstance(Context _context) {
		if (inst == null) {
			inst = new Controller(_context);
		}
		return inst;
	}

	public boolean isBusy() {
		return mBusy;
	}

	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		while (true) {
			try {
				Command command = mCommands.take();
				if ( command.listener == null || 
						isActiveListener(command.listener) || 
						(command.listener != null && command.singlenotify == true)) {
					mBusy = true;
					command.runnable.run();
					
				}
			}
			catch (Exception e) {
				if (Config.LOGD) {
					Log.d(TAG, "Error running command", e);
				}
			}
			mBusy = false;
		}
	}

	public void addListener(MessagingListener listener) {
		synchronized (mListeners) {
			mListeners.add(listener);
		}
	}

	public void removeListener(MessagingListener listener) {
		synchronized (mListeners) {
			mListeners.remove(listener);
		}
	}

	private boolean isActiveListener(MessagingListener listener) {
		synchronized (mListeners) {
			return mListeners.contains(listener);
		}
	}

	private void put(String description, MessagingListener listener, boolean singlenotify, Runnable runnable) {
		try {
			Command command = new Command();
			command.listener = listener;
			command.runnable = runnable;
			command.description = description;
			command.singlenotify = singlenotify;
			mCommands.add(command);
		}
		catch (IllegalStateException ie) {
			throw new Error(ie);
		}
	}

	class Command {
		public Runnable runnable;

		public MessagingListener listener;

		public String description;

		public boolean singlenotify;
		
		@Override
		public String toString() {
			return description;
		}
	}

	public void loadRemoteImage(final Context context, 
			final String imageUrl,
			final MessagingListener listener) {

		
		put("loadRemoteImage", listener, true, new Runnable() {
			public void run() {
				
				try {
					MustardApplication.sImageManager.put(imageUrl);
					put("loadRemoteImageFinished", listener, true, new Runnable() {
						public void run() {
							listener.loadRemoteImageFinished(context);
						}
					});
				} catch (final Exception e) {
					Log.e(TAG, e.getMessage());
				} 
			}
		});
	}
	
	public void loadRemoteText(final Context context, 
			final String textUrl,
			final MessagingListener listener) {

		
		put("loadRemoteText", listener, true, new Runnable() {
			public void run() {
				listener.loadRemoteTextStarted(context);
				try {
					HttpManager hm = new HttpManager(context);
					URL url = new URL(textUrl);
					hm.setHost(url.getHost());
					final String html = hm.getResponseAsString(textUrl, HttpManager.GET, null);
					put("loadRemoteTextFinished", listener, true, new Runnable() {
						public void run() {
							listener.loadRemoteTextFinished(context,html);
						}
					});
				} catch (final Exception e) {
					Log.e(TAG, e.getMessage());
					put("loadRemoteTextFailed", listener, true, new Runnable() {
						public void run() {
							listener.loadRemoteTextFailed(context,e.getMessage());
						}
					});
				} 
			}
		});
	}
	
	public void loadGeoNames(final Context context, 
			final String lon,
			final String lat,
			final MessagingListener listener) {
		
		put("loadGeoNames", listener, true, new Runnable() {
			public void run() {
				listener.loadGeonameStarted(context);
				try {
					final GeoName gn = GeoNames.getGeoName(context,lon, lat);
					put("loadGeoNamesFinished", listener, true, new Runnable() {
						public void run() {
							listener.loadGeonameFinished(context,gn);
						}
					});
				} catch (final Exception e) {
					Log.e(TAG, e.getMessage());
					put("loadGeoNamesFailed", listener, true, new Runnable() {
						public void run() {
							listener.loadGeonameFailed(context,e.getMessage());
						}
					});
				} 
			}
		});
		
	}

	public void loadDirectMessages(final Context context,
			final StatusNet statusNet,
			final long id,
			final boolean high,
			final int inOut,
			final MessagingListener listener) {

		
		put("loadDirectMessages", listener, true, new Runnable() {
			public void run() {
				listener.loadDirectMessagesStarted();
				try {
					ArrayList<DirectMessage> _dms = null;
					if (inOut == 0)
						_dms = statusNet.getDirectMessages(id, high);
					else
						_dms = statusNet.getDirectMessagesSent(id, high);
					final ArrayList<DirectMessage> dms = _dms;
					_dms = null;
					put("loadDirectMessagesFinished", listener, true, new Runnable() {
						public void run() {
							listener.loadDirectMessagesFinished(dms, high ? 0 : 1);
						}
					});
				} catch (final Exception e) {
					Log.e(TAG, e.getMessage());
					put("loadDirectMessagesFailed", listener, true, new Runnable() {
						public void run() {
							listener.loadDirectMessagesFailed(e.getMessage());
						}
					});
				} 
			}
		});
	}
	
	public void sendDirectMessage(final Context context,
			final StatusNet statusNet,
			final String screen_name,
			final String text,
			final MessagingListener listener) {

		
		put("sendDirectMessage", listener, true, new Runnable() {
			public void run() {
				listener.sendDirectMessageStarted();
				try {
					statusNet.sendDirectMessage(text, screen_name);
					put("loadDirectMessagesFinished", listener, true, new Runnable() {
						public void run() {
							listener.sendDirectMessageFinished();
						}
					});
				} catch (final Exception e) {
					Log.e(TAG, e.getMessage());
					put("loadDirectMessagesFailed", listener, true, new Runnable() {
						public void run() {
							listener.sendDirectMessageFailed(e.getMessage());
						}
					});
				} 
			}
		});
	}
			
}