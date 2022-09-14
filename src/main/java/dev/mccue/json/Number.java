package dev.mccue.json;

import java.math.BigDecimal;

record Number(@Override BigDecimal value) implements Json.Number {
}
