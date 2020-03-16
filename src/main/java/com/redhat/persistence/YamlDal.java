package com.redhat.persistence;

import com.redhat.constants.Constants;
import com.redhat.entities.Acronym;
import com.redhat.entities.Explanation;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class YamlDal {
    Map<String, Map<String, String>> yamlDatabase;


    public YamlDal() {
        InputStream is = this.getClass()
                .getClassLoader()
                .getResourceAsStream(Constants.YAML_SOURCE);
        this.yamlDatabase = new Yaml(new Constructor(), new Representer(),
                new DumperOptions(), new CustomResolver())
                .load(is);
    }

    public Set<Acronym> getAcronymsByName(String name) {
        Set<Acronym> res = new HashSet<>();
        for(String key : yamlDatabase.keySet()) {
            Map<String, String> databaseSection = yamlDatabase.get(key);
            if(databaseSection.containsKey(name.toUpperCase())) {
                Acronym a = new Acronym(name);
                Set<Explanation> e = new HashSet<>();
                e.add(new Explanation(databaseSection.get(name.toUpperCase())));
                a.setExplanations(e);
                res.add(a);
            }
        }
        return res;
    }

    // Custom resolver such that we don't have implicit number -> Integer or
    // number -> Float typing. We want String or null values only.
    private class CustomResolver extends Resolver {
        protected void addImplicitResolvers() {
            addImplicitResolver(Tag.MERGE, MERGE, "<");
            addImplicitResolver(Tag.NULL, EMPTY, null);
        }

    }
}

