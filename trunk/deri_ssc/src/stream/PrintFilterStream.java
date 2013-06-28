package stream;


import ssc.wrapper.TriplesDataRetriever;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


import org.dom4j.Attribute;
import org.dom4j.Element;
import org.hamcrest.core.IsInstanceOf;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * <p>This is a code example of Twitter4J Streaming API - filter method support.<br>
 * Usage: java twitter4j.examples.stream.PrintFilterStream [follow(comma separated numerical user ids)] [track(comma separated filter terms)]<br>
 * </p>
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public final class PrintFilterStream {
	static String TWITTER_ACCESS_TOKEN ="755832372-6BrszmduLdoxQAA1fsagtHsbMgzcAUIvCvbv09rQ";
	static String TWITTER_ACCESS_TOKEN_SECRET = "Jyb3Wo9O5EE7PwdkDxC7f0WmmmK9z3QDodJHQyIkr8M";
	static String TWITTER_CONSUMER_KEY = "bNksSWymSeBXXilrjvTVQ";
	static String TWITTER_CONSUMER_SECRET = "M8GqzEk1ILlhm0GKJk2aiSSqx8NafpfzD6pIaiGs";
	
	
    public static void main(String[] args) throws TwitterException {
    	ArrayList<String> filterInput = new ArrayList<String>();
//    	filterInput.add("#galway");
    	filterInput.add("weather");
//    	filterInput.add("2012");
//    	filterInput.add("killer");
//    	filterInput.add("news");
    	
    	
    	
//        if (args.length < 1) {
//            System.out.println("Usage: java twitter4j.examples.PrintFilterStream [follow(comma separated numerical user ids)] [track(comma separated filter terms)]");
//            System.exit(-1);
//        }

        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
//                System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
//                System.out.println("Hashtag" + status.getHashtagEntities());
                String rawJSON = DataObjectFactory.getRawJSON(status);
//                System.out.println(status.getText());
//                System.out.println(status.getCreatedAt().toLocaleString());
//                System.out.println(status.getUser().getName());
//                System.out.println(status.getUser().getScreenName());
//                System.out.println(status.getHashtagEntities()[0].getText());              
        		
                JSONObject json = (JSONObject) JSONSerializer.toJSON(rawJSON);
                System.out.println(json);
                System.out.println(TriplesDataRetriever.getTwitterTripleData(status));
//                System.out.println(json.get("hashtags"));
//                iteraJSON(json);
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        
        ConfigurationBuilder confbuilder  = new ConfigurationBuilder(); 
        confbuilder.setOAuthAccessToken(TWITTER_ACCESS_TOKEN) 
        .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET) 
        .setOAuthConsumerKey(TWITTER_CONSUMER_KEY) 
        .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET); 
        confbuilder.setJSONStoreEnabled(true);        
        
        TwitterStream twitterStream = new  TwitterStreamFactory(confbuilder.build()).getInstance(); 
        twitterStream.addListener(listener);
        ArrayList<Long> follow = new ArrayList<Long>();
        ArrayList<String> track = new ArrayList<String>();
        for (String arg : filterInput) {
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
        twitterStream.filter(new FilterQuery(0, followArray, trackArray));
    }

    private static boolean isNumericalArgument(String argument) {
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
    
    private static void iteraJSON(JSONObject obj){
    	Iterator iter = obj.keys();
    	while(iter.hasNext()){
    		 String key = iter.next().toString();
    		 if(obj.get(key) instanceof JSONObject)
    			 iteraJSON((JSONObject)obj.get(key));
    		 else{
    			if(key.equals("hashtags")){
    				JSONArray jArr = obj.getJSONArray(key);
    				for(int i=0;i< jArr.size();i++){
    					JSONObject o = (JSONObject)jArr.get(i);
    					System.out.println(o.get("text"));
    				}
//    				System.out.println(key+":"+obj.get(key));
    			}
    		 }
    	}
		
	}
}
