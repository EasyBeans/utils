package org.ow2.easybeans.util.osgi.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.ow2.easybeans.util.osgi.BCMapper;

import java.net.URL;

/**
 * A {@code Activator} is a {@link BundleActivator} which open a {@link BundleTracker} that tracks bundles startup
 * and register their {@link BundleContext} into the {@link BCMapper}.
 *
 * @author Loic Albertin
 */
public class Activator implements BundleActivator {
    /**
     * Root of the bundle.
     */
    private static final String ROOT = "/";
    private BundleTracker<URL> bundleTracker;

    public void start(BundleContext context) throws Exception {
        bundleTracker = new BundleTracker<URL>(context, Bundle.STARTING | Bundle.ACTIVE, null) {
            @Override
            public URL addingBundle(Bundle bundle, BundleEvent event) {
                final URL rootUrl = bundle.getEntry(ROOT);
                // rootUrl could be null for system bundle
                if (rootUrl != null) {
                    BCMapper.getInstance().put(rootUrl, bundle.getBundleContext());
                }
                return rootUrl;
            }

            @Override
            public void removedBundle(Bundle bundle, BundleEvent event, URL rootUrl) {
                if (rootUrl != null) {
                    BCMapper.getInstance().remove(rootUrl);
                }
            }
        };
        bundleTracker.open();
    }

    public void stop(BundleContext context) throws Exception {
        bundleTracker.close();
        bundleTracker = null;
    }
}
