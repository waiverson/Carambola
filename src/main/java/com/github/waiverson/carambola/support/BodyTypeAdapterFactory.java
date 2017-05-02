package com.github.waiverson.carambola.support;

import com.github.waiverson.carambola.RunnerVariablesProvider;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xyc on 2016/8/17.
 */
public class BodyTypeAdapterFactory {

    private final RunnerVariablesProvider variablesProvider;

    private Map<ContentType, BodyTypeAdapterCreator> contentTypeToBodyTypeAdapter =
            new HashMap<ContentType, BodyTypeAdapterCreator>();
    {
        BodyTypeAdapterCreator jsonBodyTypeAdapterCreator = new BodyTypeAdapterCreator() {
            @Override
            public BodyTypeAdapter createBodyTypeAdapter() {
                return new JSONBodyTypeAdapter(variablesProvider);
            }
        };
        contentTypeToBodyTypeAdapter.put(ContentType.JS, jsonBodyTypeAdapterCreator);
        contentTypeToBodyTypeAdapter.put(ContentType.JSON, jsonBodyTypeAdapterCreator);
        contentTypeToBodyTypeAdapter.put(ContentType.XML, new BodyTypeAdapterCreator() {
            @Override
            public BodyTypeAdapter createBodyTypeAdapter() {
                return new XPathBodyTypeAdapter();
            }
        });
        contentTypeToBodyTypeAdapter.put(ContentType.TEXT, new BodyTypeAdapterCreator() {
            @Override
            public BodyTypeAdapter createBodyTypeAdapter() {
                return new TextBodyTypeAdapter();
            }
        });
    }

    public BodyTypeAdapterFactory(final RunnerVariablesProvider variablesProvider) {
        this.variablesProvider = variablesProvider;
    }

    /**
     * Returns a @link {@link BodyTypeAdapter} for the given charset and @link {@link ContentType}.
     *
     * @param content the contentType
     * @param charset the charset.
     * @return an instance of {@link BodyTypeAdapter}
     */
    public BodyTypeAdapter getBodyTypeAdapter(ContentType content, String charset) {
        final BodyTypeAdapterCreator creator = contentTypeToBodyTypeAdapter.get(content);
        if (creator == null) {
            throw new IllegalArgumentException("Content-Type is UNKNOWN. Unable to find a BodyTypeAdapter to instantiate");
        }
        final BodyTypeAdapter instance = creator.createBodyTypeAdapter();
        if (charset != null) {
            instance.setCharset(charset);
        } else {
            instance.setCharset(Charset.defaultCharset().name());
        }
        return instance;
    }

    interface BodyTypeAdapterCreator {
        BodyTypeAdapter createBodyTypeAdapter();
    }
}
