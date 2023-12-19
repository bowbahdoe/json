package dev.mccue.json;

import dev.mccue.json.internal.JsonIntegerImpl;

public sealed abstract class JsonInteger
        extends JsonNumber
        permits JsonIntegerImpl {
}
