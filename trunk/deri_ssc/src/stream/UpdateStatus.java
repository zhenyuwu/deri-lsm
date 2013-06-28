package stream;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;


public class UpdateStatus {
	String TWITTER_ACCESS_TOKEN ="755832372-6BrszmduLdoxQAA1fsagtHsbMgzcAUIvCvbv09rQ";
	private String TWITTER_ACCESS_TOKEN_SECRET = "Jyb3Wo9O5EE7PwdkDxC7f0WmmmK9z3QDodJHQyIkr8M";
	private String TWITTER_CONSUMER_KEY = "bNksSWymSeBXXilrjvTVQ";
	private String TWITTER_CONSUMER_SECRET = "M8GqzEk1ILlhm0GKJk2aiSSqx8NafpfzD6pIaiGs";
	
	
	
	public void postToTwitter(String message) {
        try {
            ConfigurationBuilder confbuilder  = new ConfigurationBuilder(); 
            confbuilder.setOAuthAccessToken(TWITTER_ACCESS_TOKEN) 
            .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET) 
            .setOAuthConsumerKey(TWITTER_CONSUMER_KEY) 
            .setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET); 
            confbuilder.setJSONStoreEnabled(true);
            Twitter twitter = new TwitterFactory(confbuilder.build()).getInstance(); 

//            Status status = twitter.updateStatus(message);
//            System.out.println("Successfully updated the status to [" + status.getText() + "].");
            
//            System.setProperty("jsonStoreEnabled", "true");
            List<Status> statuses = twitter.getHomeTimeline();
            for (Status status : statuses) {
                String rawJSON = DataObjectFactory.getRawJSON(status);                              
                System.out.println(rawJSON );
            }

//            Query query = new Query("viet nam");
//            QueryResult result = twitter.search(query);
//            for (Tweet tweet : result.getTweets()) {
//                System.out.println(tweet.getFromUser() + ":" + tweet.getText());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UpdateStatus us = new UpdateStatus();
		us.postToTwitter("test from java app");
	}

}
