package ox.augmented.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import ox.augmented.model.Poi;
import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class TwitterSource implements DataSource {

	private Poi anchor;
	private String hashtags;
	private Twitter twitter;
	private final String TWITTER_CONSUMER_KEY = "2rsIk9k5FXTgTPcK5L8yfw2";
	private final String TWITTER_CONSUMER_SECRET= "jVigCods792wKx1SGRaI8zoXG4MZYpuwMCkgH4k";
	private List<twitter4j.Status> statuses;
	private int statusIndex = 0;
	public TwitterSource(Poi newAnchor, String newHashtags) {
		this.anchor = newAnchor;
		this.hashtags = newHashtags;
        twitter = new TwitterFactory().getInstance();
        AsyncTask<Boolean, Void, Boolean> testUpdate = new AsyncTask<Boolean, Void, Boolean>() {

        	@Override
            protected Boolean doInBackground(Boolean... bools) { 
        		try {
        		    Query query = new Query(hashtags);
        		    query.setGeoCode(new GeoLocation(anchor.getLatitude(), anchor.getLongitude()),0.1d,Query.KILOMETERS);
        		    QueryResult result = twitter.search(query);
        		    statuses = result.getTweets();
	                // gets Twitter instance with default credentials
	                /*twitter = new TwitterFactory().getInstance();
	                User user = twitter.verifyCredentials();
	                List<twitter4j.Status> statuses = twitter.getHomeTimeline();
	                System.out.println("Showing @" + user.getScreenName() + "'s home timeline.");
	                for (twitter4j.Status status : statuses) {
	                    System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
	                }*/
        		} catch (TwitterException te) {
	                te.printStackTrace();
	                System.out.println("Failed to get timeline: " + te.getMessage());
	                return false;
        		}
        		return true;
        		}
        	};
        	
        testUpdate.execute();
	}
	
	public TwitterSource(Poi anchor) {
		this(anchor, "");
	}

	public TwitterSource(String hashtags) {
		//Changed from this(null, "");
		this(null, hashtags);
	}
	
	@Override
	public Poi getAnchor() {
		return anchor;	
	}

	@Override
	public void setAnchor(Poi newAnchor) {
		this.anchor = newAnchor;
	}

	@Override
	public Bitmap getCurrentImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentText() {
		if(statuses != null && !statuses.isEmpty())
			return statuses.get(statusIndex).getText();
		else
			return "";
	}

	@Override
	public void next() {
		if(statuses != null && !statuses.isEmpty()) {
			statusIndex = (statusIndex + 1) % statuses.size();
		}
	}

	@Override
	public void previous() {
		if(statuses != null && !statuses.isEmpty()) {
			statusIndex = (statusIndex - 1) % statuses.size();
		}
	}
	
	@Override
	public String getSearchString(){
		return hashtags;
	}

}
