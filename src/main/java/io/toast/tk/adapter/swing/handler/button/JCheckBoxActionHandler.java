package io.toast.tk.adapter.swing.handler.button;

import javax.swing.JCheckBox;

import org.fest.swing.fixture.JCheckBoxFixture;

import io.toast.tk.adapter.swing.handler.ISwingWidgetActionHandler;
import io.toast.tk.adapter.swing.utils.FestRobotInstance;
import io.toast.tk.core.net.request.CommandRequest;
import io.toast.tk.dao.domain.api.test.ITestResult.ResultKind;


public class JCheckBoxActionHandler implements ISwingWidgetActionHandler<JCheckBox, String, CommandRequest>{

	@Override
	public String handle(
		JCheckBox checkbox,
		CommandRequest command) {
		switch(command.action) {
			case SET :
				Boolean value = Boolean.valueOf(command.value);
				checkbox.setSelected(value);
				return ResultKind.SUCCESS.name();
			case GET :
				return String.valueOf(checkbox.isSelected());
			default :
				throw new IllegalArgumentException("Unsupported command for JCheckBox: " + command.action.name());
		}
	}
}
