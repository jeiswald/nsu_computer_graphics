package ru.nsu.fit.ejsvald.setting;

public class Setting {
    private final String name;
    private int value;

    public Setting(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
