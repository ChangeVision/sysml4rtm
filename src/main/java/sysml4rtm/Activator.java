package sysml4rtm;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sysml4rtm.validation.ValidationRuleManagerImpl;
import validation.view.ModelValidationViewLocator;

import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandlerFactory;

public class Activator implements BundleActivator {

	private static IMessageDialogHandler messageHandler;
	private static ModelValidationViewLocator locator;
	private static final Logger logger = LoggerFactory.getLogger(Activator.class);

	public void start(final BundleContext context) {
		logger.debug("Activator#start");

		try {
			getModaliValidationViewLocatorReference(context);

			ServiceReference reference = context
					.getServiceReference(IMessageDialogHandlerFactory.class.getName());
			IMessageDialogHandlerFactory factory = (IMessageDialogHandlerFactory) context
					.getService(reference);
			if (factory != null) {
				messageHandler = factory.createMessageDialogHandler(new Messages(),
						"\\.astah\\sysml\\sysml4rtm.log");
			}

			context.ungetService(reference);
			logger.debug("Activator#end");

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void getModaliValidationViewLocatorReference(final BundleContext context) {
		try {
			context.addServiceListener(new ServiceListener() {

				@Override
				public void serviceChanged(ServiceEvent event) {
					ServiceReference ref = event.getServiceReference();
					locator = (ModelValidationViewLocator) context.getService(ref);
					locator.addValidationRuleManager(new ValidationRuleManagerImpl());

				}
			}, "(objectClass=" + ModelValidationViewLocator.class.getName() + ")");
		} catch (InvalidSyntaxException e) {
			logger.error("unexpected error", e);
		}

		ServiceTracker serviceTracker = new ServiceTracker(context,
				ModelValidationViewLocator.class.getName(), null);
		serviceTracker.open();
		if (serviceTracker != null) {
			ModelValidationViewLocator e = (ModelValidationViewLocator) serviceTracker.getService();
			if (e != null) {
				locator.addValidationRuleManager(new ValidationRuleManagerImpl());
			}
		}
		serviceTracker.close();
	}

	public void stop(BundleContext context) {
	}

	public static ModelValidationViewLocator getModaliValidationViewLocator() {
		return locator;
	}

	public static IMessageDialogHandler getMessageHandler() {
		return messageHandler;
	}

}
