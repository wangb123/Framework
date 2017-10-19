
package org.wbing.base.view.toast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huigu, <huigu@mogujie.com>
 * @Date 2014-6-19
 */

public class MGPinkToastManager {
    private static MGPinkToastManager instance;
    private Map<String, PinkToast> currentShowingToasts;

    public MGPinkToastManager() {
        currentShowingToasts = new ConcurrentHashMap<>();
    }

    public static MGPinkToastManager getInstance() {
        if (null == instance) {
            instance = new MGPinkToastManager();
        }
        return instance;
    }

    synchronized public void addCurrentShowingToast(PinkToast pinkToast) {
        if (!hasSameShowingToast(pinkToast)) {
            currentShowingToasts.put(pinkToast.getContent(), pinkToast);
        }
    }

    synchronized public void removeFromCurrentShowingToasts(PinkToast pinkToast) {
        if (hasSameShowingToast(pinkToast)) {
            currentShowingToasts.remove(pinkToast.getContent());
        }
    }

    public void hideAllShowingToasts() {
        for (PinkToast pinkToast : currentShowingToasts.values()) {
            pinkToast.cancel();
        }
    }

    public boolean hasSameShowingToast(PinkToast pinkToast) {
        return currentShowingToasts.containsKey(pinkToast.getContent());
    }
}
