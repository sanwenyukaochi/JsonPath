package com.jayway.jsonpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface ParseContext {

    DocumentContext parse(String json);

    DocumentContext parse(Object json);

    DocumentContext parse(InputStream json);

    DocumentContext parse(InputStream json, String charset);

    DocumentContext parse(File json) throws IOException;

    @Deprecated
    DocumentContext parse(URL json) throws IOException;
}
