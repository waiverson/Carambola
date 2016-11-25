package com.github.waiverson.carambola;

import com.github.waiverson.carambola.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smartrics.rest.client.RestClient;
import smartrics.rest.client.RestData.Header;
import smartrics.rest.client.RestRequest;
import smartrics.rest.client.RestResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by waiverson on 2016/8/3.
 */


public class Carambola implements StatementExecutorConsumer, RunnerVariablesProvider{

    private Runner runner;

    private Config config;

    private StatementExecutorInterface DslStatementExecutor;

    private PartsFactory partsFactory;

    private Url baseUrl;

    private String lastEvaluation;

    private Map<String, String> defaultHeaders = new HashMap<String, String>();

    protected Map<String,String> requestHeaders;

    private RestRequest lastRequest;

    private RestResponse lastResponse;

    private Map<String, String> namespaceContext = new HashMap<String, String>();

    private CellFormatter<?> formatter;

    private boolean displayActualOnRight = true;

    private boolean displayAbsoluteURLInFull = true;

    private int minlenForCollapseToggle = -1;

    protected boolean resourceUrisAreEscaped = false;

    private boolean followRedirects = true;

    private boolean debugMethodCall = false;

    private RestClient restClient;

    protected Variables GLOBALS;

    protected RowWrapper row;

    private static final Logger LOG = LoggerFactory.getLogger(Carambola.class);

    private static final String LINE_SEPARATOR = "\n";

    protected String requestBody;

    protected String fileName = null;

    protected String multipartFileName = null;

    protected String multipartFileParameterName = FILE;

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

    public CellFormatter<?> getFormatter() {
        return formatter;
    }


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

        minlenForCollapseToggle = config.getAsInteger(
                "carambola.display.toggle.for.cells.larger.than",
                minlenForCollapseToggle);

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

    private void setLastRequest(RestRequest lastRequest) {
        this.lastRequest = lastRequest;
    }

    protected RestRequest getLastRequest() {
        return lastRequest;
    }

    private void setLastResponse(RestResponse  lastResponse){
        this.lastResponse = lastResponse;
    }

    protected RestResponse getLastResponse() {
        return lastResponse;
    }

    private void configRestClient() {
        restClient = partsFactory.buildRestClient(config);
    }

    private void renderReplacement(CellWrapper cell, String actual) {
        StringTypeAdapter adapter = new StringTypeAdapter();
        adapter.set(actual);
        if (!adapter.equals(actual, cell.body())) {
            cell.body(actual);
            getFormatter().right(cell, adapter);
        }
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

    private void processDslRow(List<List<String>> resultTable, List<String> row) {
        RowWrapper currentRow = new DslRow(row);
        try {
            processRow(currentRow);
        }
        catch (Exception e) {
            LOG.error("Exception raised when processing row " + row.get(0), e);
            getFormatter().exception(currentRow.getCell(0), e);
        } finally {
            List<String> rowAsList = mapDslRow(row, currentRow);
            resultTable.add(rowAsList);
        }
    }

    public void processRow(RowWrapper<?> currentRow) {
        row = currentRow;
        CellWrapper cell0 = row.getCell(0);
        if (cell0 ==null) {
            throw new RuntimeException("Current row is not parseable (maybe empty or not existent)");
        }
        String methodName = cell0.text();
        if ("".equals(methodName)) {
            throw new RuntimeException("method not specified");
        }
        Method method1;
        try {
            method1 = getClass().getMethod(methodName);
            method1.invoke(this);
        }catch (SecurityException e) {
            throw new RuntimeException(
                    "Not enough permissions to access method " + methodName
                            + " for this class "
                            + this.getClass().getSimpleName(), e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Class " + this.getClass().getName()
                    + " doesn't have a callable method named " + methodName, e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Method named " + methodName
                    + " invoked with the wrong argument.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Method named " + methodName
                    + " is not public.", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Method named " + methodName
                    + " threw an exception when executing.", e);
        }
    }

    private List<String> mapDslRow(List<String> resultRow, RowWrapper  currentRow) {
        List<String> rowAsList = ((DslRow)currentRow).asList();
        for (int c =0; c < rowAsList.size(); c++) {
            String v = rowAsList.get(c);
            if (v.equals(resultRow.get(c))) {
                rowAsList.set(c, "");
            }
        }
        return rowAsList;
    }

    protected Map<String, String> parseHeaders(String str) {
        return Tools.convertStringToMap(str, ":", LINE_SEPARATOR, true);
    }

    private Map<String, String> parseNamespaceContext(String str) {
        return Tools.convertStringToMap(str, "=", LINE_SEPARATOR, true);
    }

    private String deHtmlify(String someHtml) {
        return Tools.fromHtml(someHtml);
    }



    /**
     * Allows setting of the name of the file to upload.
     *
     * <code>| setFileName | Name of file |</code>
     * <p/>
     * body text should be location of file which needs to be sent
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setFileName() {
        CellWrapper cell = row.getCell(1);
        if (cell == null) {
            getFormatter().exception(row.getCell(0),
                    "You must pass a file name to set");
        } else {
            fileName = GLOBALS.substitute(cell.text());
            renderReplacement(cell, fileName);
        }
    }

    /**
     * @return the filename
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Allows setting of the name of the multi-part file to upload.
     *
     * <code>| setMultipartFileName | Name of file |</code>
     * <p/>
     * body text should be location of file which needs to be sent
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setMultipartFileName() {
        CellWrapper cell = row.getCell(1);
        if (cell == null) {
            getFormatter().exception(row.getCell(0),
                    "You must pass a multipart file name to set");
        } else {
            multipartFileName = GLOBALS.substitute(cell.text());
            renderReplacement(cell, multipartFileName);
        }
    }

    /**
     * @return the multipart filename
     */
    public String getMultipartFileName() {
        return multipartFileName;
    }


    /**
     * Sets the parameter to send in the request storing the multi-part file to
     * upload. If not specified the default is <code>file</code>
     * <p/>
     * <code>| setMultipartFileParameterName | Name of form parameter for the uploaded file |</code>
     * <p/>
     * body text should be the name of the form parameter, defaults to 'file'
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setMultipartFileParameterName() {
        CellWrapper cell = row.getCell(1);
        if (cell == null) {
            getFormatter().exception(row.getCell(0),
                    "You must pass a parameter name to set");
        } else {
            multipartFileParameterName = GLOBALS.substitute(cell.text());
            renderReplacement(cell, multipartFileParameterName);
        }
    }

    /**
     * @return the multipart file parameter name.
     */
    public String getMultipartFileParameterName() {
        return multipartFileParameterName;
    }

    /**
     * <code> | GET | uri | ?ret | ?headers | ?body |</code>
     * <p/>
     * executes a GET on the uri and checks the return (a string repr the
     * operation return code), the http response headers and the http response
     * body
     *
     * uri is resolved by replacing vars previously defined with
     * <code>let()</code>
     *
     * the http request headers can be set via <code>setHeaders()</code>. If not
     * set, the list of default headers will be set. See
     * <code>DEF_REQUEST_HEADERS</code>
     */
    public void GET() {
        debugMethodCallStart();
        doMethod("Get");
        debugMethodCallEnd();
    }

    public void HEAD() {
        debugMethodCallStart();
        doMethod("Head");
        debugMethodCallEnd();
    }

    public void PUT() {
        debugMethodCallStart();
        doMethod(emptifyBody(requestBody), "Put");
        debugMethodCallEnd();
    }

    public void OPTIONS() {
        debugMethodCallStart();
        doMethod("Options");
        debugMethodCallEnd();
    }

    public void DELETE() {
        debugMethodCallStart();
        doMethod("Delete");
        debugMethodCallEnd();
    }

    public void TRACE() {
        debugMethodCallStart();
        doMethod("Trace");
        debugMethodCallEnd();
    }

    public void POST() {
        debugMethodCallStart();
        doMethod(emptifyBody(requestBody), "Post");
        debugMethodCallEnd();
    }


    /**
     * dsl: | let | label | type | loc | expr |
     * allows to associate a value to a label. values are extracted from the
     * body of the last successful http response.
     * example:
     * <code> | let | id | body | /services/id[0]/text() | | <code/>
     * <code> | GET | /services/%id% | 200 | | | <code/>
     */
    public void let() {
        debugMethodCallStart();
        if(row.size() != 5) {
            getFormatter().exception(row.getCell(row.size() - 1), "Not all cells found: | let | label | type | expr | result |");
            debugMethodCallEnd();
            return;
        }
        String label = row.getCell(1).text().trim();
        String loc = row.getCell(2).text();
        CellWrapper exprCell = row.getCell(3);
        try {
            exprCell.body(GLOBALS.substitute(exprCell.body()));
            String expr = exprCell.text();
            CellWrapper valueCell = row.getCell(4);
            String valueCellText = valueCell.body();
            String valueCellTextReplaced = GLOBALS.substitute(valueCellText);
            valueCell.body(valueCellTextReplaced);
            String sValue = null;
            LetHandler letHandler = LetHandlerFactory.getHandlerFor(loc);

        }
        catch ();
    }


    private void doMethod(String m) {
        doMethod(null, m);
    }

    protected void doMethod(String body, String method) {
        CellWrapper urlCell = row.getCell(1);
        String url = deHtmlify(stripTag(urlCell.text()));
        String resUrl = GLOBALS.substitute(url);
        String rBody = GLOBALS.substitute(body);
        Map<String, String> rHeaders = subsititute(getHeaders());
        try{
            doMethod(method, resUrl, rHeaders, rBody);
            completeHttpMethodExecution();
        } catch (RuntimeException e) {
            getFormatter().exception(
                    row.getCell(0),
                    "Execution of" + method + "caused exception '"
                    + e.getMessage() + "'"
            );
            LOG.error("Exception occurred when processing method=" + method, e);
        }
    }

    protected void doMethod(String method, String resUrl, String rBody) {
        doMethod(method, resUrl, subsititute(getHeaders()), rBody);
    }

    protected void doMethod(String method, String resUrl, Map<String,String> headers,String rBody) {
        setLastRequest(partsFactory.buildRestRequest());
        getLastRequest().setMethod(RestRequest.Method.valueOf(method));
        getLastRequest().addHeaders(headers);
        getLastRequest().setFollowRedirect(followRedirects);
        getLastRequest().setResourceUriEscaped(resourceUrisAreEscaped);
        if (fileName != null) {
            getLastRequest().setFileName(fileName);
        }
        if (multipartFileName != null){
            getLastRequest().setMultipartFileName(multipartFileName);
        }
        getLastRequest().setMultipartFileParameterName(
                multipartFileParameterName);
        String[] uri = resUrl.split("\\?", 2);

        String[] thisRequestUrlParts = buildThisRequestUrl(uri[0]);
        getLastRequest().setResource(thisRequestUrlParts[1]);
        if (uri.length > 1) {
            String query = uri[1];
            for (int i=2; i<uri.length; i++) {
                query += "?" + uri[i]; //TODO: StringBuilder
            }
            getLastRequest().setQuery(query);
        }
        if ("Post".equals(method) || "Put".equals(method)) {
            getLastRequest().setBody(rBody);
        }

        configureCredentials();

        restClient.setBaseUrl(thisRequestUrlParts[0]);
        RestResponse response = restClient.execute(getLastRequest());
        setLastResponse(response);

    }

    protected void completeHttpMethodExecution() {
        String uri = getLastResponse().getResource();
        String query = getLastRequest().getQuery();
        if(query != null && !"".equals(query.trim())) {
            uri = uri + "?" + query;
        }
        String clientBaseUri = restClient.getBaseUrl();
        String u = clientBaseUri + uri;
        CellWrapper uriCell = row.getCell(1);
        getFormatter().asLink(uriCell, GLOBALS.substitute(uriCell.body()), u, uri);
        CellWrapper cellStatusCode = row.getCell(2);
        if (cellStatusCode == null) {
            throw new IllegalArgumentException("you must specify a status code cell");
        }
        Integer lastStatusCode = getLastResponse().getStatusCode();
        process(cellStatusCode, lastStatusCode.toString(), new StatusCodeTypeAdapter());
        List<Header> lastHeaders = getLastResponse().getHeaders();
        process(row.getCell(3), lastHeaders, new HeadersTypeAdapter());
        CellWrapper bodyCell = row.getCell(4);
        if (bodyCell == null) {
            throw new IllegalArgumentException("you must specify a body cell");
        }
        bodyCell.body(GLOBALS.substitute(bodyCell.body()));
        BodyTypeAdapter bodyTypeAdapter = createBodyTypeAdapter();
        process(bodyCell, getLastResponse().getBody(), bodyTypeAdapter);
    }

    private void process(CellWrapper expected, Object actual, RestDataTypeAdapter ta){
        if (expected == null) {
           throw new IllegalArgumentException("you must specify a headers cell");
        }
        ta.set(actual);
        boolean ignore = "".equals(expected.text().trim());
        if (ignore){
            String actualString = ta.toString();
            if (!"".equals(actualString)) {
                expected.addToBody(getFormatter().gray(actualString));
            }
        }
        else {
            boolean success = false;
            try {
                String substitue = GLOBALS.substitute(Tools.fromHtml(expected.text()));
                Object parse = ta.parse(substitue);
                success = ta.equals(parse, actual);
            } catch (Exception e) {
                getFormatter().exception(expected, e);
                return;
            }
            if (success) {
                getFormatter().right(expected, ta);
            } else {
                getFormatter().wrong(expected, ta);
            }
        }
    }

    protected BodyTypeAdapter createBodyTypeAdapter() {
        return createBodyTypeAdapter(ContentType.parse(getLastResponse().getContentType()));
    }

    protected BodyTypeAdapter createBodyTypeAdapter(ContentType ct) {
        String charset = getLastResponse().getCharset();
        BodyTypeAdapter bodyTypeAdapter = partsFactory.buildBodyTypeAdapter(ct, charset);
        bodyTypeAdapter.setContext(namespaceContext);
        return bodyTypeAdapter;
    }

    private Map<String, String> subsititute(Map<String, String> headers) {
        Map<String,  String> sub = new HashMap<String, String>();
        for (Map.Entry<String, String> e : headers.entrySet()) {
            sub.put(e.getKey(), GLOBALS.substitute(e.getValue()));
        }
        return sub;
    }

    public Map<String, String> getHeaders() {
        Map<String,String> headers = null;
        if (requestHeaders != null) {
            headers = requestHeaders;
        }else {
            headers = defaultHeaders;
        }
        return headers;
    }

    private void debugMethodCallStart() {
        debugMethodCall("=>");
    }

    private void debugMethodCallEnd() {
        debugMethodCall("<=");
    }

    private void debugMethodCall(String h) {
        if (debugMethodCall) {
            StackTraceElement el = Thread.currentThread().getStackTrace()[4];
            LOG.debug(h + el.getMethodName());
        }
    }

    protected String emptifyBody(String b) {
        String body = b;
        if (body == null) {
            body = "";
        }
        return body;
    }

    private String[] buildThisRequestUrl(String uri) {
        String[] parts = new String[2];
        if(baseUrl == null || uri.startsWith(baseUrl.toString())) {
            Url url = new Url(uri);
            parts[0] = url.getBaseUrl();
            parts[1] = url.getResource();
        }else {
            try{
                Url attemped = new Url(uri);
                parts[0] = attemped.getBaseUrl();
                parts[1] = attemped.getResource();
            } catch (RuntimeException e) {
                parts[0] = baseUrl.toString();
                parts[1] = uri;
            }
        }
        return parts;
    }

    private void configureCredentials() {
        String username = config.get("http.basicauth.username");
        String password = config.get("http.basicauth.password");
        if (username != null && password != null) {
            String newUsername = GLOBALS.substitute(username);
            String newPassword = GLOBALS.substitute(password);
            Config newConfig = getConfig();
            newConfig.add("http.basicauth.username", newUsername);
            newConfig.add("http.basicauth.password", newPassword);
            restClient = partsFactory.buildRestClient(newConfig);
        }
    }

}
