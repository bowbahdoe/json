package dev.mccue.json;

import java.io.Serial;
import java.io.Serializable;

record JsonSerializationProxy(java.lang.String json) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Serial
    public java.lang.Object readResolve() {
        return Json.readString(json);
    }
}
