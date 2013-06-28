package	deri.sensor.google.hub.published;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class TestPublish {

	public static void testPublisher() throws Exception {
		Publisher publisher = new Publisher();
		String hub = "http://pubsubhubbub.appspot.com/publish";
		//String hub="http://lsmHub.deri.org/userfeed";
		//String hub="http://myhub.example.com/endpoint";
		int status = publisher
				.execute(hub,
						"http://weather.yahooapis.com/forecastrss?w=1378250");
		System.out.println("Return status : " + status);
	}

	public static void main(String[] args) {
		try { 
			testPublisher();
			Discovery discovery=new Discovery();
			System.out.println(discovery.getHub("http://weather.yahooapis.com/forecastrss?w=1378250"));
			//System.out.println(discovery.getHub("http://publisher.example.com/topic.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
