package sonia.scm.plugins.csb;

import java.io.IOException;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.PostReceiveRepositoryHook;
import sonia.scm.repository.RepositoryHookEvent;

@Extension
public class CsbDeployPostReceiveHook extends PostReceiveRepositoryHook {

	public void onEvent(RepositoryHookEvent event) {
		try {
			String url = String.format("http://localhost:8080/csb/rest/%s/deploy", event.getRepository().getName());
			DefaultHttpClient client = new DefaultHttpClient();
			client.getCredentialsProvider().setCredentials(new AuthScope("localhost", 8080),
					new UsernamePasswordCredentials("portal", "qwerty321"));
			HttpPost post = new HttpPost(url);
			client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}