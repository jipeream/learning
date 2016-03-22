/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  twitter4j.StallWarning
 *  twitter4j.Status
 *  twitter4j.StatusDeletionNotice
 *  twitter4j.StatusListener
 */
package es.jipeream.library.twitter;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public abstract class TwitterStatusListener
implements StatusListener {
    public abstract void onStatus(Status var1);

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
    }

    public void onScrubGeo(long userId, long upToStatusId) {
    }

    public void onStallWarning(StallWarning warning) {
    }

    public void onException(Exception ex) {
    }
}

