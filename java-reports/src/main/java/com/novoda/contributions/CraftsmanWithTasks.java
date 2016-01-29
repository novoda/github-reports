package com.novoda.contributions;

import java.util.List;

public class CraftsmanWithTasks {
    private final  String name;
    public List<ApiTasks.ApiTask> tasks;

    public CraftsmanWithTasks(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CraftsmanWithTasks{" +
                "name='" + name + '\'' +
                ", tasks=" + tasks +
                '}';
    }
}
