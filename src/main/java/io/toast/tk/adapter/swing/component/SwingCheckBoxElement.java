package io.toast.tk.adapter.swing.component;

import java.util.UUID;

import io.toast.tk.adapter.swing.SwingAutoElement;
import io.toast.tk.adapter.web.HasClickAction;
import io.toast.tk.adapter.web.HasStringValue;
import io.toast.tk.core.driver.IRemoteSwingAgentDriver;
import io.toast.tk.core.net.request.CommandRequest;
import io.toast.tk.core.runtime.ISwingElementDescriptor;
import io.toast.tk.dao.domain.api.test.ITestResult;
import io.toast.tk.dao.domain.api.test.ITestResult.ResultKind;

public class SwingCheckBoxElement extends SwingAutoElement implements HasClickAction,  HasStringValue {

	public SwingCheckBoxElement(
		ISwingElementDescriptor element,
		IRemoteSwingAgentDriver driver) {
		super(element, driver);
	}

	public SwingCheckBoxElement(
		ISwingElementDescriptor element) {
		super(element);
	}

	public ITestResult select()
		throws Exception {
		boolean exists = exists();
		final String requestId = UUID.randomUUID().toString();
		ITestResult result = frontEndDriver.processAndWaitForValue(new CommandRequest.CommandRequestBuilder(requestId).with(wrappedElement.getLocator())
			.ofType(wrappedElement.getType().name()).sendKeys("true").build());
		result.setResultKind(exists && result.getMessage().equals(ResultKind.SUCCESS.name()) ? ResultKind.SUCCESS : ResultKind.ERROR);
		return result;
	}

	public ITestResult deselect()
		throws Exception {
		boolean exists = exists();
		final String requestId = UUID.randomUUID().toString();
		ITestResult result = frontEndDriver.processAndWaitForValue(new CommandRequest.CommandRequestBuilder(requestId).with(wrappedElement.getLocator())
			.ofType(wrappedElement.getType().name()).sendKeys("false").build());
		result.setResultKind(exists && result.getMessage().equals(ResultKind.SUCCESS.name()) ? ResultKind.SUCCESS : ResultKind.ERROR);
		return result;
	}

	@Override
	public ITestResult click()
		throws Exception{
		boolean exists = exists();
		final String requestId = UUID.randomUUID().toString();
		ITestResult result = frontEndDriver.processAndWaitForValue(new CommandRequest.CommandRequestBuilder(requestId).with(wrappedElement.getLocator())
				.ofType(wrappedElement.getType().name()).click().build());
		result.setResultKind(exists && result.getMessage().equals(ResultKind.SUCCESS.name()) ? ResultKind.SUCCESS : ResultKind.ERROR);
		return result;
	}

	@Override
	public void dbClick() {
	}

	@Override
	public String getValue()
		throws Exception{
		exists();
		final String requestId = UUID.randomUUID().toString();
		CommandRequest request = new CommandRequest.CommandRequestBuilder(requestId).with(wrappedElement.getLocator())
			.ofType(wrappedElement.getType().name()).getValue().build();
		return frontEndDriver.processAndWaitForValue(request).getMessage();
	}
}
