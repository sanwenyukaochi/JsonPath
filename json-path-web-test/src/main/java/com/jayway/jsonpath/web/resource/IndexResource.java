package com.jayway.jsonpath.web.resource;

import static java.util.Arrays.asList;

import com.jayway.jsonpath.web.bench.Bench;
import com.jayway.jsonpath.web.bench.Result;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.server.mvc.Viewable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class IndexResource {

    private static final Logger logger = LoggerFactory.getLogger(IndexResource.class);

    public static final Map<String, String> TEMPLATES = loadTemplates();

    @GET
    @Path("templates/{template}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemplate(@PathParam("template") @DefaultValue("goessner") String template) {
        return Response.ok(TEMPLATES.get(template)).build();
    }

    @POST
    @Path("eval")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemplate(
            @FormParam("json") String json,
            @FormParam("path") String path,
            @FormParam("type") String type,
            @FormParam("flagWrap") boolean flagWrap,
            @FormParam("flagMerge") boolean flagMerge,
            @FormParam("flagSuppress") boolean flagSuppress) {

        boolean value = "VALUE".equalsIgnoreCase(type);

        Map<String, Result> resultMap =
                new Bench(json, path, value, flagWrap, flagMerge, flagSuppress).runAll();

        return Response.ok(resultMap).build();
    }

    @GET
    public Viewable get(@QueryParam("template") @DefaultValue("goessner") String template) {
        return createView(TEMPLATES.get(template), "$.store.book[0].title", true, template, null);
    }

    private Viewable createView(
            String json,
            String path,
            boolean value,
            String selectedTemplate,
            List<Result> results) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("results", results);
        res.put("json", json);
        res.put("path", path);
        res.put("value-checked", value ? "checked" : "");
        res.put("path-checked", (!value) ? "checked" : "");
        res.put(
                "templates",
                asList(
                        new Template("goessner", "goessner", selectedTemplate),
                        new Template("twitter", "twitter", selectedTemplate),
                        new Template("webapp", "webapp", selectedTemplate),
                        new Template("20k", "20k", selectedTemplate)));

        return new Viewable("/index", res);
    }

    private static Map<String, String> loadTemplates() {
        try {
            String goessner =
                    IOUtils.toString(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResourceAsStream("json/goessner.json"),
                            "UTF-8");
            String twitter =
                    IOUtils.toString(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResourceAsStream("json/twitter.json"));
            String webapp =
                    IOUtils.toString(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResourceAsStream("json/webxml.json"));
            String twentyK =
                    IOUtils.toString(
                            Thread.currentThread()
                                    .getContextClassLoader()
                                    .getResourceAsStream("json/20k.json"));

            Map<String, String> templates = new HashMap<String, String>();
            templates.put("blank", "null");
            templates.put("goessner", goessner);
            templates.put("twitter", twitter);
            templates.put("webapp", webapp);
            templates.put("20k", twentyK);

            return templates;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class Template {
        public final String value;
        public final String name;
        public final String selected;

        private Template(String value, String name, String selectedValue) {
            this.value = value;
            this.name = name;
            this.selected = value.equalsIgnoreCase(selectedValue) ? "selected" : "";
        }
    }
}
