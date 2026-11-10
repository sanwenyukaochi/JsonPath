package com.jayway.jsonpath.spi.mapper;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;

import java.util.ArrayList;

public class TapestryMappingProvider implements MappingProvider {

    @Override
    public <T> T map(
            final Object source, final Class<T> targetType, final Configuration configuration) {
        if (source == null) {
            return null;
        }
        if (targetType.isAssignableFrom(source.getClass())) {
            return (T) source;
        }
        try {
            if (targetType.isAssignableFrom(ArrayList.class)
                    && configuration.jsonProvider().isArray(source)) {
                int length = configuration.jsonProvider().length(source);
                @SuppressWarnings("rawtypes")
                ArrayList list = new ArrayList(length);
                for (Object o : configuration.jsonProvider().toIterable(source)) {
                    list.add(o);
                }
                return (T) list;
            }
        } catch (Exception e) {

        }
        throw new MappingException(
                "Cannot convert a "
                        + source.getClass().getName()
                        + " to a "
                        + targetType
                        + " use Tapestry's TypeCoercer instead.");
    }

    @Override
    public <T> T map(
            final Object source, final TypeRef<T> targetType, final Configuration configuration) {
        throw new UnsupportedOperationException(
                "Tapestry JSON provider does not support TypeRef! Use a Jackson or Gson based provider");
    }
}
