package io.toast.tk.adapter.swing.handler.menu;

import javax.swing.JMenu;

import org.fest.swing.core.MouseButton;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JMenuItemFixture;
import org.fest.swing.fixture.JPopupMenuFixture;

import io.toast.tk.adapter.swing.handler.ActionProcessor;
import io.toast.tk.adapter.swing.utils.FestRobotInstance;
import io.toast.tk.core.net.request.CommandRequest;
import io.toast.tk.dao.domain.api.test.ITestResult;

class JMenuSelectActionProcessor implements ActionProcessor<JMenu> {

	@Override
	public String processCommandOnComponent(
		CommandRequest command,
		JMenu target) {
		Robot robot = FestRobotInstance.getRobot();
		if(target == null) {
			robot.pressMouse(MouseButton.RIGHT_BUTTON);
		}
		else {
			robot.click(target);
		}
		JPopupMenuFixture popupFixture = new JPopupMenuFixture(robot, robot.findActivePopupMenu());
		String[] menuPath = computeMenuItemFixturePath(command.value);
		JMenuItemFixture menuItemWithPath = popupFixture.menuItemWithPath(menuPath);
		if(menuItemWithPath != null && menuItemWithPath.component().isEnabled()) {
			menuItemWithPath.click();
			return ITestResult.ResultKind.SUCCESS.name();
		}
		else {
			return ITestResult.ResultKind.FAILURE.name();
		}
	}
	
	public static final String[] computeMenuItemFixturePath(String commandValue){
		String[] itemPath = null;
		if(commandValue==null){
			return null;
		}
		if(commandValue.contains(" / ")){
			itemPath = commandValue.split(" / ");
		} else {
			itemPath = new String[]{commandValue};
		}
		return itemPath;
	}
}
