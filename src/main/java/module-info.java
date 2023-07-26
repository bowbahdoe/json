import org.jspecify.annotations.NullMarked;

@SuppressWarnings("module")
@NullMarked
module dev.mccue.json {
    requires static transitive org.jspecify;

    exports dev.mccue.json;
    exports dev.mccue.json.stream;
}