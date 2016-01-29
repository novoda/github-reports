package com.novoda.contributions;

import java.util.List;

public class CraftsmanWithTasks {
    public String name;
    public List<ApiTasks.ApiTask> tasks;

    @Override
    public String toString() {
        return "CraftsmanWithTasks{" +
                "name='" + name + '\'' +
                ", tasks=" + tasks +
                '}';
    }
}
