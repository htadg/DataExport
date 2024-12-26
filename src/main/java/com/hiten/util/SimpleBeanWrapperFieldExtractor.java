package com.hiten.util;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class SimpleBeanWrapperFieldExtractor implements FieldExtractor<Map<String, String>>, InitializingBean {

    private String[] names;

    public void setNames(String[] names) {
        Assert.notNull(names, "Names must be non-null");
        this.names = names.clone();
    }

    @Override
    public Object[] extract(@NonNull Map<String, String> item) {
        List<Object> values = new ArrayList<>();
        for(String propertyName : this.names) {
            values.add(item.get(propertyName.strip()));
        }
        return values.toArray();
    }

    public void afterPropertiesSet() {
        Assert.state(this.names != null, "The 'names' property must be set.");
    }

}
