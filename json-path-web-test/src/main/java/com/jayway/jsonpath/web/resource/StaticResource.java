package com.jayway.jsonpath.web.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import java.io.InputStream;

@Path("/static/")
public class StaticResource {

    @GET
    @Path("fonts/{resource}")
    public InputStream getFonts(@Context UriInfo uriInfo) {
        return Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("fonts/" + uriInfo.getPathParameters().getFirst("resource"));
    }

    @GET
    @Path("js/{resource}")
    @Produces("text/javascript")
    public InputStream getJs(@Context UriInfo uriInfo) {
        return Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("js/" + uriInfo.getPathParameters().getFirst("resource"));
    }

    @GET
    @Path("css/{resource}")
    @Produces("text/css")
    public InputStream getCss(@Context UriInfo uriInfo) {
        return Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("css/" + uriInfo.getPathParameters().getFirst("resource"));
    }
}
