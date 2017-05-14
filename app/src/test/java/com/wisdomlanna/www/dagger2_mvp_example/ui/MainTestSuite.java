package com.wisdomlanna.www.dagger2_mvp_example.ui;

import com.wisdomlanna.www.dagger2_mvp_example.ui.frangment.MainFragmentPresenterTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CalculatorMetricPlusTest.class,
        CalculatorMetricMinusTest.class,
        CalculatorMetricDivideTest.class,
        CalculatorMetricMultiplyTest.class,
        MainPresenterTest.class,
        MainFragmentPresenterTest.class
})
public class MainTestSuite {
}
