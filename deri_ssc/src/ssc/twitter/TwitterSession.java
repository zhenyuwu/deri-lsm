package ssc.twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import ssc.rabbitmq.Pusher;
import ssc.wrapper.TriplesDataRetriever;
import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

public class TwitterSession {
	static String TWITTER_ACCESS_TOKEN ="755832372-6BrszmduLdoxQAA1fsagtHsbMgzcAUIvCvbv09rQ";
	static String TWITTER_ACCESS_TOKEN_SECRET = "Jyb3Wo9O5EE7PwdkDxC7f0WmmmK9z3QDodJHQyIkr8M";
	static String TWITTER_CONSUMER_KEY = "bNksSWymSeBXXilrjvTVQ";
	static String TWITTER_CONSUMER_SECRET = "M8GqzEk1ILlhm0GKJk2aiSSqx8NafpfzD6pIaiGs";
	
	private TwitterStream twitterStream;
	private StatusListener listener;
	private String filterInput;
	private final String EXCHANGE_NAME = "Twitter";
	private Pusher pusher;
	
	private Twitter twitter;
	
	public TwitterSession(){
//		listener = new StatusListener() {
//	        public void onStatus(Status status) {
//	            String rawJSON = DataObjectFactory.getRawJSON(status);
//	            JSONObject json = (JSONObject) JSONSerializer.toJSON(rawJSON);
////	            System.out.println(json);
//	            String triple = TriplesDataRetriever.getTwitterTripleData(status);
//	            try {
//					pusher.push(triple);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//	        }
//
//	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
//	            System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
//	        }
//
//	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
//	            System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
//	        }
//
//	        public void onScrubGeo(long userId, long upToStatusId) {
//	            System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
//	        }
//
//	        public void onException(Exception ex) {
//	            ex.printStackTrace();
//	        }
//	    }; 
	}	
	
	
	
	public String getFilterInput() {
		return filterInput;
	}



	public void setFilterInput(String filterInput) {
		this.filterInput = filterInput;
	}



	public void init(){
		ConfigurationBuilder confbuilder  = new ConfigurationBuilder(); 
	    confbuilder.setOAuthAccessToken(TWITTER_ACCESS_TOKEN) 
	    .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET) 
	    .setOAuthConsumerKey(TWITTER_CONSUMER_KEY) 
	    .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET); 
	    confbuilder.setJSONStoreEnabled(true);        
	    
	    String[] filterArr = filterInput.split(",");
    	
    	
	    twitterStream = new  TwitterStreamFactory(confbuilder.build()).getInstance(); 
	    twitterStream.addListener(listener);
	    ArrayList<Long> follow = new ArrayList<Long>();
	    ArrayList<String> track = new ArrayList<String>();
	    for (String arg : filterArr) {
	        if (isNumericalArgument(arg)) {
	            for (String id : arg.split(",")) {
	                follow.add(Long.parseLong(id));
	            }
	        } else {
	            track.addAll(Arrays.asList(arg.split(",")));
	        }
	    }
	    long[] followArray = new long[follow.size()];
	    for (int i = 0; i < follow.size(); i++) {
	        followArray[i] = follow.get(i);
	    }
	    String[] trackArray = track.toArray(new String[track.size()]);
	    // filter() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.
	    
	    pusher = new Pusher();
	    pusher.setEXCHANGE_NAME(EXCHANGE_NAME);
	    pusher.openRabbitConnection();
	    
	    twitterStream.filter(new FilterQuery(0, followArray, trackArray));		
	}
	
	public void intitOneShotQuery(){
		ConfigurationBuilder confbuilder  = new ConfigurationBuilder(); 
	    confbuilder.setOAuthAccessToken(TWITTER_ACCESS_TOKEN) 
	    .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET) 
	    .setOAuthConsumerKey(TWITTER_CONSUMER_KEY) 
	    .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET); 
	    confbuilder.setJSONStoreEnabled(true); 
	    
	    twitter = new TwitterFactory(confbuilder.build()).getInstance();	    
	}
	
	public String getOneShotResult(String filter){
		String result = "";
		try {        	
			if(filter.equals("")) return result;
            QueryResult resultTw = twitter.search(new Query(filter));
            List<Tweet> tweets = resultTw.getTweets();
            for (Tweet tweet : tweets) {
                result+= TriplesDataRetriever.getTwitterTripleData(tweet)+"\n";
            }            
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
            System.exit(-1);
        }		
		return result;
	}
	   
	public void stopStream(){
		twitterStream.shutdown();
	}
	
	public void resetFilter(String newFillter){
		filterInput = newFillter;
		String[] filterArr = filterInput.split(",");
		
		ArrayList<Long> follow = new ArrayList<Long>();
	    ArrayList<String> track = new ArrayList<String>();
	    for (String arg : filterArr) {
	        if (isNumericalArgument(arg)) {
	            for (String id : arg.split(",")) {
	                follow.add(Long.parseLong(id));
	            }
	        } else {
	            track.addAll(Arrays.asList(arg.split(",")));
	        }
	    }
	    long[] followArray = new long[follow.size()];
	    for (int i = 0; i < follow.size(); i++) {
	        followArray[i] = follow.get(i);
	    }
	    String[] trackArray = track.toArray(new String[track.size()]);
	    
		twitterStream.cleanUp();
		twitterStream.filter(new FilterQuery(0, followArray, trackArray));
	}
	
	
	private boolean isNumericalArgument(String argument) {
	    String args[] = argument.split(",");
	    boolean isNumericalArgument = true;
	    for (String arg : args) {
	        try {
	            Integer.parseInt(arg);
	        } catch (NumberFormatException nfe) {
	            isNumericalArgument = false;
	            break;
	        }
	    }
	    return isNumericalArgument;
	}		
}
