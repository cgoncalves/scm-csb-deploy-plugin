package sonia.scm.plugins.csb;

import sonia.scm.plugin.ext.Extension;
import sonia.scm.repository.PostReceiveRepositoryHook;
import sonia.scm.repository.RepositoryHookEvent;

@Extension
public class CsbDeployPostReceiveHook extends PostReceiveRepositoryHook {

	public void onEvent(RepositoryHookEvent event) {
	}
}
