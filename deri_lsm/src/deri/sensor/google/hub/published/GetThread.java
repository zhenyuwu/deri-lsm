package deri.sensor.google.hub.published;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
/**
 * @author Hoan Nguyen Mau Quoc
 */
final class GetThread extends Thread {

	private final HttpClient httpClient;
	private final HttpContext context;
	private final HttpPost httppost;
	public HttpResponse httpresponse = null;

	public GetThread(HttpClient httpClient, HttpPost httppost) {
		this.httpClient = httpClient;
		this.context = new BasicHttpContext();
		this.httppost = httppost;
	}

	@Override
	public void run() {
		try {
			httpresponse = this.httpClient.execute(this.httppost, this.context);
			HttpEntity entity = httpresponse.getEntity();
			if (entity != null) {
				System.out.println(httpresponse.getStatusLine());
				entity.consumeContent();
			}
		} catch (Exception ex) {
			this.httppost.abort();
		}
	}


}