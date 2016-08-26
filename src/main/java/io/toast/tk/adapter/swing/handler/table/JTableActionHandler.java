package io.toast.tk.adapter.swing.handler.table;

import javax.swing.JTable;

import org.fest.swing.data.TableCell;
import org.fest.swing.data.TableCellByColumnId;
import org.fest.swing.fixture.JTableCellFixture;
import org.fest.swing.fixture.JTableFixture;

import io.toast.tk.adapter.swing.handler.ISwingWidgetActionHandler;
import io.toast.tk.adapter.swing.utils.FestRobotInstance;
import io.toast.tk.core.net.request.CommandRequest;
import io.toast.tk.core.net.request.TableCommandRequest;
import io.toast.tk.core.net.request.TableCommandRequestQueryCriteria;
import io.toast.tk.dao.domain.api.test.ITestResult.ResultKind;

public class JTableActionHandler implements
	ISwingWidgetActionHandler<JTable, String, CommandRequest> {

	@Override
	public String handle(JTable target, final CommandRequest command) {
		JTableFixture tFixture = new JTableFixture(FestRobotInstance.getRobot(), target);
		
		switch (command.action) {
		case COUNT:
			return handleCountAction(tFixture);
		case FIND:
			return handleFindAction((TableCommandRequest) command, tFixture);
		case OPEN_MENU:
			return handleOpenMenu(tFixture);
		case DOUBLE_CLICK:
			return handleDoubleClickAction((TableCommandRequest) command, tFixture);
		case SELECT_MENU:
			return handleSelectMenu((TableCommandRequest) command, tFixture);
		default:
			throw new IllegalArgumentException("Unsupported command for JTable: " + command.action.name());
		}
	}

	private String handleOpenMenu(final JTableFixture tFixture) {
		FestRobotInstance.runOutsideEDT(new Runnable() {			
			@Override
			public void run() {
				try{					
					tFixture.showPopupMenu();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		return ResultKind.SUCCESS.name();
	}

	private String handleSelectMenu(final TableCommandRequest command,
			JTableFixture tFixture) {
		for (int i = 0; i < tFixture.rowCount(); i++) {
			int totalFound = 0;
			boolean found = findRowByCriteria(tFixture, command, i, totalFound);
			if (found) {
				JTableCellFixture cell = tFixture.cell(TableCell.row(i).column(1));
				selectCellPopupMenuItem(command, cell);
				return String.valueOf(i);
			}
		}
		return "No row matching provided criteria !";
	}

	private void selectCellPopupMenuItem(final TableCommandRequest command,
			JTableCellFixture cell) {
		FestRobotInstance.runOutsideEDT(new Runnable() {
			@Override
			public void run() {
				cell.showPopupMenu().menuItemWithPath(command.value).click();					
			}
		});
	}

	private String handleCountAction(JTableFixture tFixture) {
		int tries = 30;
		while (tFixture.rowCount() == 0 && tries > 0) {
			try {
				tries--;
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return String.valueOf(tFixture.rowCount());
	}

	private String handleDoubleClickAction(final TableCommandRequest command,
			JTableFixture tFixture) {
		TableCommandRequest tcommand;
		tcommand = (TableCommandRequest) command;
		boolean found = false;
		for (int i = 0; i < tFixture.rowCount(); i++) {
			int totalFound = 0;
			found = findRowByCriteria(tFixture, tcommand, i, totalFound);
			if (found) {
				JTableCellFixture cell = tFixture.cell(TableCell.row(i).column(
						1));
				cell.select();
				cell.doubleClick();
				return null;
			}
		}
		return "No row matching provided criteria !";
	}

	private String handleFindAction(final TableCommandRequest command,
			JTableFixture tFixture) {
		TableCommandRequest tcommand = (TableCommandRequest) command;
		if (tcommand.query.criteria.size() == 0) {
			return "No Criteria to select a row !";
		}
		if (tFixture.rowCount() == 0) {
			return "The table is empty !";
		}
		for (int i = 0; i < tFixture.rowCount(); i++) {
			int totalFound = 0;
			boolean found = findRowByCriteria(tFixture, tcommand, i, totalFound);
			if (found) {
				if (tcommand.query.resultCol != null) {
					JTableCellFixture cell = tFixture.cell(TableCellByColumnId.row(i).columnId(tcommand.query.resultCol));
					cell.select();
					return cell.value();
				} else {
					try {
						tFixture.selectRows(i);
					} catch (Exception ex) {
					}
					return String.valueOf((i + 1));
				}
			}
		}
		return "No row matching provided criteria !";
	}

	private boolean findRowByCriteria(JTableFixture tFixture,
			TableCommandRequest tcommand, int i, int totalFound) {
		for (TableCommandRequestQueryCriteria criterion : tcommand.query.criteria) {
			JTableCellFixture cell = tFixture.cell(TableCellByColumnId.row(i)
					.columnId(criterion.lookupCol));
			if (cell.value().equals(criterion.lookupValue)) {
				totalFound++;
			}
			if (totalFound == tcommand.query.criteria.size()) {
				return true;
			}
		}
		return false;
	}
}
