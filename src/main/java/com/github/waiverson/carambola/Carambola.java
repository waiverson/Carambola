package com.github.waiverson.carambola;

import com.github.waiverson.carambola.support.Config;
import com.github.waiverson.carambola.support.*;

/**
 * Created by Administrator on 2016/8/3.
 */
public class Carambola implements StatementExecutorConsumer, RunnerVariablesProvider{

    private Runner runner;
    private Config config;
    private StatementExecutorInterface DslStatementExecutor;
    private PartsFactory partsFactory;

    public enum Runner {
        DSL,
        TABLE,
        OTHER;
    }

    @Override
    public Variables createRunnerVariables() {
        switch (runner) {
            case DSL:
                return new DslVariables(config, DslStatementExecutor);
            case TABLE:
                return TableVariables(config);
            default:
                return new DslVariables(config);
        }
    }

    public Carambola() {
        super();
        this.partsFactory = new PartsFactory(this);
    }



}
