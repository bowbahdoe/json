package dev.mccue.json;

enum False implements Json.Boolean {
    INSTANCE;

    @Override
    public boolean value() {
        return false;
    }

    @Override
    public java.lang.String toString() {
        return "false";
    }
}
