package com.jayway.jsonpath.spi.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Deprecated
public interface HttpProvider {

    InputStream get(URL url) throws IOException;
}
