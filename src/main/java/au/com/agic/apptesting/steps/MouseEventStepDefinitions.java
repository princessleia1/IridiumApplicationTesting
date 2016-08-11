package au.com.agic.apptesting.steps;

import au.com.agic.apptesting.State;
import au.com.agic.apptesting.constants.Constants;
import au.com.agic.apptesting.utils.*;
import au.com.agic.apptesting.utils.impl.GetByImpl;
import au.com.agic.apptesting.utils.impl.JavaScriptRunnerImpl;
import au.com.agic.apptesting.utils.impl.SimpleWebElementInteractionImpl;
import au.com.agic.apptesting.utils.impl.SleepUtilsImpl;
import cucumber.api.java.en.When;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * Gherkin steps for sending javascript events to elements.
 *
 * These steps have Atom snipptets that start with the prefix "mouse".
 * See https://github.com/mcasperson/iridium-snippets for more details.
 */
public class MouseEventStepDefinitions {
	private static final Logger LOGGER = LoggerFactory.getLogger(MouseEventStepDefinitions.class);
	private static final GetBy GET_BY = new GetByImpl();
	private static final SimpleWebElementInteraction SIMPLE_WEB_ELEMENT_INTERACTION =
		new SimpleWebElementInteractionImpl();
	private static final SleepUtils SLEEP_UTILS = new SleepUtilsImpl();
	private static final JavaScriptRunner JAVA_SCRIPT_RUNNER = new JavaScriptRunnerImpl();

	/**
	 * Get the web driver for this thread
	 */
	private final FeatureState featureState =
		State.THREAD_DESIRED_CAPABILITY_MAP.getDesiredCapabilitiesForThread();

	/**
	 * Some applications use mouse events instead of clicks, and PhantomJS will often need us to supply these
	 * events manually. This step uses simple selection.
	 *
	 * @param event         The mouse event we want to generate (mousedown, mouseup etc)
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I(?: dispatch a)? ?\"(mouse.*?)\"(?: event)? on (?:a|an|the) hidden element found by( alias)? \"([^\"]*)\"( if it exists)?$")
	public void mouseEventSimpleHiddenElementStep(
		final String event,
		final String alias,
		final String selectorValue,
		final String exists) throws ExecutionException, InterruptedException {

		try {
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebElement element = SIMPLE_WEB_ELEMENT_INTERACTION.getClickableElementFoundBy(
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			/*
				Just like the click, sometimes we need to trigger mousedown events manually
			 */
			JAVA_SCRIPT_RUNNER.interactHiddenElementMouseEvent(element, event, js);
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}

	/**
	 * Some applications use mouse events instead of clicks, and PhantomJS will often need us to supply these
	 * events manually.
	 *
	 * @param event         The mouse event we want to generate (mousedown, mouseup etc)
	 * @param selector      Either ID, class, xpath, name or css selector
	 * @param alias         If this word is found in the step, it means the selectorValue is found from the
	 *                      data set.
	 * @param selectorValue The value used in conjunction with the selector to match the element. If alias was
	 *                      set, this value is found from the data set. Otherwise it is a literal value.
	 * @param exists        If this text is set, an error that would be thrown because the element was not
	 *                      found is ignored. Essentially setting this text makes this an optional statement.
	 */
	@When("^I(?: dispatch a)? ?\"(mouse.*?)\"(?: event)? on (?:a|an|the) hidden element with (?:a|an|the) "
		+ "(ID|class|xpath|name|css selector)( alias)? of \"([^\"]*)\"( if it exists)?$")
	public void mouseEventHiddenElementStep(
		final String event,
		final String selector,
		final String alias,
		final String selectorValue,
		final String exists) {

		try {
			final By by = GET_BY.getBy(
				selector,
				StringUtils.isNotBlank(alias),
				selectorValue,
				featureState);
			final WebDriver webDriver = State.THREAD_DESIRED_CAPABILITY_MAP.getWebDriverForThread();
			final WebDriverWait wait = new WebDriverWait(webDriver, Constants.WAIT);
			final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
			final JavascriptExecutor js = (JavascriptExecutor) webDriver;

			/*
				Just like the click, sometimes we need to trigger mousedown events manually
			 */
			JAVA_SCRIPT_RUNNER.interactHiddenElementMouseEvent(element, event, js);
			SLEEP_UTILS.sleep(featureState.getDefaultSleep());
		} catch (final TimeoutException | NoSuchElementException ex) {
			if (StringUtils.isBlank(exists)) {
				throw ex;
			}
		}
	}
}
