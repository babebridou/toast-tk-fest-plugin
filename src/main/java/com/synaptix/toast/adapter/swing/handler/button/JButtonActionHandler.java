package com.synaptix.toast.adapter.swing.handler.button;

import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.synaptix.toast.adapter.swing.handler.ISwingWidgetActionHandler;
import com.synaptix.toast.adapter.swing.utils.FestRobotInstance;
import com.synaptix.toast.core.net.request.CommandRequest;
import com.synaptix.toast.dao.domain.api.test.ITestResult.ResultKind;

public class JButtonActionHandler implements
			ISwingWidgetActionHandler<JButton, String, CommandRequest> {
	

	private static final Logger LOG = LogManager.getLogger(JButtonActionHandler.class);

	@Override
	public String handle(final JButton button, CommandRequest command) {
		switch (command.action) {
		case CLICK:
			//FIX for possible lock on AWT thread when the button click disposes the window containing the button
			// attention FestRobotInstance must not be started on EDT
			// the problem is the deadlock when the button click disposes 
			// the window containing the button in a certain way
			// happens with some SNCF dialog windows
			if(SwingUtilities.isEventDispatchThread()){
				try{
					FestRobotInstance.getRobot().click(button);
					return ResultKind.SUCCESS.name();
				} catch(Exception e){
					LOG.warn(e.getMessage(), e);
					return ResultKind.SUCCESS.name();
				}
			} else {
				final CountDownLatch latch = new CountDownLatch(1);
				FestRobotInstance.runOutsideEDT(new Runnable() {
					@Override
					public void run() {
						button.doClick();
						latch.countDown();
					}
				});
				try {
					latch.await();
					return ResultKind.SUCCESS.name();
				} catch (InterruptedException e) {
					LOG.error(e.getMessage(), e);
					return ResultKind.ERROR.name();
				}
			}
		case ACTIVE:
			if(button.isEnabled()){
				return ResultKind.SUCCESS.name();
			}else{
				return ResultKind.ERROR.name();
			}
		default:
			throw new IllegalArgumentException("Unsupported command for JButton: " + command.action.name());
		}
	}
}
