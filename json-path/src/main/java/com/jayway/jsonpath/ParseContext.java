package com.jayway.jsonpath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface ParseContext {

    ReadContext parse(String json);

    ReadContext parse(Object json);

    ReadContext parse(InputStream json);

    ReadContext parse(InputStream json, String charset);

    ReadContext parse(File json) throws IOException;

    ReadContext parse(URL json) throws IOException;
}
