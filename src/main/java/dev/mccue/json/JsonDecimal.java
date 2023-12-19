package dev.mccue.json;

import dev.mccue.json.internal.JsonDecimalImpl;

public sealed abstract class JsonDecimal
        extends JsonNumber
        permits JsonDecimalImpl {
}
