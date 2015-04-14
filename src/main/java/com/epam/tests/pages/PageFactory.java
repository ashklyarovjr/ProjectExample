package com.epam.tests.pages;

import org.jbehave.web.selenium.PropertyWebDriverProvider;


public class PageFactory {

    private PropertyWebDriverProvider driverProvider;

    public PageFactory(PropertyWebDriverProvider driverProvider) {
        this.driverProvider = driverProvider;
    }

    public SearchPage newSearchPage() {
        return new SearchPage(driverProvider);
    }

}
