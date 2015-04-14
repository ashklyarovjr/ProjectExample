package com.epam.examples.runner;

import com.epam.examples.steps.SearchSteps;
import com.epam.tests.pages.PageFactory;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.embedder.executors.SameThreadExecutors;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.failures.PendingStepStrategy;
import org.jbehave.core.io.LoadFromRelativeFile;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.web.selenium.*;

import java.util.List;

import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.io.CodeLocations.codeLocationFromPath;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.web.selenium.WebDriverHtmlOutput.WEB_DRIVER_HTML;

public class SearchTestRunnerAdvanced extends JUnitStories {

    public static final String STORIES_RELATIVE_DIRECTORY = "src/main/stories";
    public static final String REPORT_RELATIVE_DIRECTORY = "../build/reports/jbehave";
    public static final String STORY_TO_EXCLUDE = "**/exclude_*.story";
    /**
     * Configuration variables
     */

    PendingStepStrategy pendingStepStrategy = new FailingUponPendingStep();
    CrossReference crossReference = new CrossReference().withJsonOnly()
            .withXmlOnly().withOutputAfterEachStory(true);
    StoryReporterBuilder reporterBuilder = new StoryReporterBuilder()
            .withCodeLocation(codeLocationFromClass(SearchTestRunner.class))
            .withFailureTrace(true).withFailureTraceCompression(true)
            .withFormats(Format.XML, Format.STATS, Format.CONSOLE, Format.HTML)
            .withCrossReference(crossReference);
    ContextView contextView = new LocalFrameContextView().sized(640, 120);
    SeleniumContext seleniumContext = new SeleniumContext();
    Format[] formats = new Format[]{
            new SeleniumContextOutput(seleniumContext), CONSOLE,
            WEB_DRIVER_HTML};
    private PropertyWebDriverProvider driverProvider = new PropertyWebDriverProvider();
    private WebDriverSteps lifecycleSteps = new PerStoriesWebDriverSteps(
            driverProvider);
    private PageFactory pageFactory = new PageFactory(driverProvider);
    public SearchTestRunnerAdvanced() {
        if (lifecycleSteps instanceof PerStoriesWebDriverSteps) {
            configuredEmbedder().useExecutorService(new SameThreadExecutors().create(
                    configuredEmbedder().embedderControls()));
        }
        configuredEmbedder().embedderControls().useStoryTimeoutInSecs(
                1000);
    }

    @Override
    public Configuration configuration() {
        return new SeleniumConfiguration()
                .useSeleniumContext(seleniumContext)
                .useWebDriverProvider(driverProvider)
                .usePendingStepStrategy(pendingStepStrategy)
                .useStoryControls(
                        new StoryControls().doResetStateBeforeScenario(true))
                .useStoryLoader(
                        new LoadFromRelativeFile(
                                codeLocationFromPath(STORIES_RELATIVE_DIRECTORY)))
                .useStepMonitor(crossReference.getStepMonitor())
                .useStoryReporterBuilder(reporterBuilder);

    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        Configuration configuration = configuration();
        return new InstanceStepsFactory(configuration, new SearchSteps(
                pageFactory), lifecycleSteps, new WebDriverScreenshotOnFailure(
                driverProvider, configuration.storyReporterBuilder()));
    }

    @Override
    protected List<String> storyPaths() {
        String storyToInclude = "**/" + System.getProperty("story", "*")
                + ".story";
        return new StoryFinder().findPaths(
                codeLocationFromPath(STORIES_RELATIVE_DIRECTORY),
                storyToInclude, STORY_TO_EXCLUDE);
    }

}
