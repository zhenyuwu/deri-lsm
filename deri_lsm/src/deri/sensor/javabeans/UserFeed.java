package deri.sensor.javabeans;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserFeed {
	private String id;
	private String feedURL;
	private String query;
	private String feedType;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuery() {
		return query;
	}	
	public String getFeedURL() {
		return feedURL;
	}
	public void setFeedURL(String feedURL) {
		this.feedURL = feedURL;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getFeedType() {
		return feedType;
	}
	public void setFeedType(String feedType) {
		this.feedType = feedType;
	}	
}
