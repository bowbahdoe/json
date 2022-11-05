package dev.mccue.json;

enum Null implements Json.Null {
    INSTANCE;

    @Override
    public java.lang.String toString() {
        return "null";
    }
}
