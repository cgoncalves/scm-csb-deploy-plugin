package sonia.scm.plugins.csb;

import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.PostReceiveRepositoryHook;
import sonia.scm.repository.RepositoryHookEvent;

@Extension
public class CsbDeployPostReceiveHook extends PostReceiveRepositoryHook {

	private static final String APPS_HOST;
	private static final String APPS_PORT;
	private static final String APPS_ENDPOINT;
	private static final String CSB_USERNAME;
	private static final String CSB_PASSWORD;

	static {
		Properties props = new Properties();
		try {
			props.load(CsbDeployPostReceiveHook.class.getResourceAsStream("/csb.properties"));
		} catch (Exception ex) {
			throw new RuntimeException("csb.properties resource is not available");
		}

		APPS_HOST = props.getProperty("apps_host");
		APPS_PORT = props.getProperty("apps_port");
		APPS_ENDPOINT = props.getProperty("apps_endpoint");
		CSB_USERNAME = props.getProperty("csb.username");
		CSB_PASSWORD = props.getProperty("csb.password");
	}

	public void onEvent(RepositoryHookEvent event) {
		String app = event.getRepository().getName();
		try {
			String url = String.format("http://%s:%s%s/%s/deploy", APPS_HOST, APPS_PORT, APPS_ENDPOINT, app);
			System.out.println("Processing CsbDeployPostReceiveHook for application " + app + "(" + url + ")...");

			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope(APPS_HOST, Integer.parseInt(APPS_PORT)),
					new UsernamePasswordCredentials(CSB_USERNAME, CSB_PASSWORD));

			// Create AuthCache instance
			// Add AuthCache to the execution context
			AuthCache authCache = new BasicAuthCache();
			BasicScheme basicAuth = new BasicScheme();
			authCache.put(new HttpHost(APPS_HOST, Integer.parseInt(APPS_PORT)), basicAuth);
			BasicHttpContext localcontext = new BasicHttpContext();
			localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);

			HttpPut put = new HttpPut(url);
			HttpResponse response = client.execute(put, localcontext);
			System.out.println("Processed CsbDeployPostReceiveHook for application " + app + ". Output status: "
					+ response.getStatusLine().getStatusCode());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}