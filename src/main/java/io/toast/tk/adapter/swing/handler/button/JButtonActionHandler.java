package io.toast.tk.adapter.swing.handler.button;

import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.toast.tk.adapter.swing.handler.ISwingWidgetActionHandler;
import io.toast.tk.adapter.swing.utils.FestRobotInstance;
import io.toast.tk.core.net.request.CommandRequest;
import io.toast.tk.dao.domain.api.test.ITestResult.ResultKind;

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
				return handleClickOnEDT(button, command);
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
	
	private String handleClickOnEDT(final JButton button, CommandRequest command){
		if(button.isEnabled()&&button.isShowing()){
			FestRobotInstance.runOutsideEDT(new Runnable(){
				@Override
				public void run() {
					FestRobotInstance.getRobot().click(button);
				}
			});
			return ResultKind.SUCCESS.name();
		}else {
			return ResultKind.FAILURE.name();
		}
	}
}
