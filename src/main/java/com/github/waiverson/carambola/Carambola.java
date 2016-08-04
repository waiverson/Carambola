package com.github.waiverson.carambola;

import com.github.waiverson.carambola.support.Config;
import com.github.waiverson.carambola.support.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Callable;

/**
 * Created by Administrator on 2016/8/3.
 */
public class Carambola implements StatementExecutorConsumer, RunnerVariablesProvider{

    private Runner runner;
    private Config config;
    private StatementExecutorInterface DslStatementExecutor;
    private PartsFactory partsFactory;
    private Url baseUrl;
    private String lastEvaluation;
    private Map<String, String> defaultHeaders = new HashMap<String, String>();
    private CellFormatter<?> formatter;
    private boolean displayActualOnRight = true;
    private boolean displayAbsoluteURLInFull = true;
    private int minlenForCollapseToggle = -1;
    protected boolean resourceUrisAreEscaped = false;
    private boolean followRedirects = true;

    protected Variables GLOBALS;

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

    public Carambola(String hostName) {
        this(hostName,Config.DEFAULT_CONFIG_NAME);
    }

    public Carambola(String hostName, String configName) {
        this.partsFactory = new PartsFactory(this);
        this.config = Config.getConfig(configName);
        this.baseUrl = new Url(stripTag(hostName));
    }

    public Config getConfig() {return config;}

    public String getLastEvaluation() {return lastEvaluation; }

    public Map<String,String> getDefaultHeader() {
        return defaultHeaders;
    }

    public String getBaseUrl() {
        if (baseUrl != null) {
            return baseUrl.toString();
        }
        return null;
    }

    public void setBaseUrl(Url url) {this.baseUrl = url; }

    public CellFormatter<?> getFormatter() {return formatter;}


    private String stripTag(String somewthingwithinATag) {
        return Tools.fromSimpleTag(somewthingwithinATag);
    }

    protected void initialize(Runner runner) {
        this.runner = runner;
        boolean state = validateState();
        notifyInvaildState(state);
        configFormatter();
        configCarambola();
        configRestClient();
    }

    private void configCarambola() {

        GLOBALS = createRunnerVariables();

        displayActualOnRight = config.getAsBoolean(
                "carambola.display.actual.on.right", displayActualOnRight);

        displayAbsoluteURLInFull = config.getAsBoolean(
                "carambola.display.absolute.url.in.full", displayAbsoluteURLInFull);

        resourceUrisAreEscaped = config
                .getAsBoolean("carambola.resource.uris.are.escaped",
                        resourceUrisAreEscaped);

        followRedirects = config.getAsBoolean(
                "carambola.requests.follow.redirects", followRedirects);

        minLenForCollapseToggle = config.getAsInteger(
                "carambola.display.toggle.for.cells.larger.than",
                minLenForCollapseToggle);

        String str = config.get("carambola.default.headers", "");
        defaultHeaders = parseHeaders(str);

        str = config.get("carambola.xml.namespace.context", "");
        namespaceContext = parseNamespaceContext(str);

        ContentType.resetDefaultMapping();
        ContentType.config(config);
    }

    private void configFormatter() {
        formatter = partsFactory.buildCellFormatter(runner);
    }

    protected void notifyInvaildState(boolean state) {
        if (!state) {
            throw new RuntimeException(
                    "you must specify a base url in the |start|, after the carambola to start"
            );
        }
    }

    protected boolean validateState() {
        return baseUrl !=null;
    }

    public List<List<String>> doDsl(List<List<String>> rows) {
        initialize(Runner.DSL);
        List<List<String>> res = new Vector<List<String>>();
        getFormatter().setDisplayActual(displayActualOnRight);
        getFormatter().setDisplayAbsoluteURLInFull(displayAbsoluteURLInFull);
        getFormatter().setMinLengthForToggleCollapse(minlenForCollapseToggle);
        for (List<String> r : rows) {
            processDslRow(res, r);
        }
        return res;
    }




}
