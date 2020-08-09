package com.example;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import org.rapidoid.RapidoidThing;
import org.rapidoid.http.Req;
import org.rapidoid.http.customize.BeanParameterFactory;
import org.rapidoid.http.customize.HttpRequestBodyParser;
import org.rapidoid.http.customize.HttpResponseRenderer;

import java.io.OutputStream;
import java.util.Map;

public class DslJsonConverter extends RapidoidThing implements HttpResponseRenderer, HttpRequestBodyParser, BeanParameterFactory {

    private final DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().includeServiceLoader());

    @Override
    public void render(Req req, Object value, OutputStream out) throws Exception {
        dslJson.serialize(value, out);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, ?> parseRequestBody(Req req, byte[] body) throws Exception {
        return null;
    }

    @Override
    public Object getParamValue(Req req, Class<?> paramType, String paramName, Map<String, Object> properties) throws Exception {
        byte[] body = req.body();
        return dslJson.deserialize(paramType, body, body.length);
    }
}
