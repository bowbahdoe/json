package dev.mccue.json;

enum True implements Json.Boolean {
    INSTANCE;

    @Override
    public boolean value() {
        return true;
    }

    @Override
    public java.lang.String toString() {
        return "True";
    }
}
