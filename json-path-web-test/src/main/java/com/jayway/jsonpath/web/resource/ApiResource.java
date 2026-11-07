package com.jayway.jsonpath.web.resource;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.web.bench.Bench;
import com.jayway.jsonpath.web.bench.Result;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class ApiResource {

    private static final Logger logger = LoggerFactory.getLogger(ApiResource.class);

    @GET
    @Path("/validate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validate(@QueryParam("path") String path) {
        int result = -1;
        try {
            JsonPath compiled = JsonPath.compile(path);
            result = compiled.isDefinite() ? 0 : 1;
        } catch (Exception e) {
        }
        return Response.ok(Collections.singletonMap("result", result)).build();
    }

    @POST
    @Path("/eval")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTemplate(
            @FormParam("json") String json,
            @FormParam("path") String path,
            @FormParam("type") String type,
            @FormParam("flagWrap") boolean flagWrap,
            @FormParam("flagMerge") boolean flagMerge,
            @FormParam("flagNullLeaf") boolean flagNullLeaf,
            @FormParam("flagSuppress") boolean flagSuppress) {

        boolean value = "VALUE".equalsIgnoreCase(type);

        Map<String, Result> resultMap =
                new Bench(json, path, value, flagWrap, flagMerge, flagSuppress, flagNullLeaf)
                        .runAll();

        return Response.ok(resultMap).build();
    }
}
