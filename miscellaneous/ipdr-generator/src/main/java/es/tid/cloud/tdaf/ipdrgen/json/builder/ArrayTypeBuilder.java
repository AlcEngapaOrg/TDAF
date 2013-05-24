package es.tid.cloud.tdaf.ipdrgen.json.builder;

import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;

import es.tid.cloud.tdaf.ipdrgen.JsonGeneratorException;
import es.tid.cloud.tdaf.ipdrgen.json.JSchemaArrayType;
import es.tid.cloud.tdaf.ipdrgen.json.JSchemaType;

public class ArrayTypeBuilder extends TypeBuilder {
    public ArrayTypeBuilder(JSonSchemaParserContext parser, JsonNode node, String defaultUri, File parent) {
        super(parser, node, defaultUri, parent);
    }

    public ArrayTypeBuilder(JSonSchemaParserContext parser, JsonNode node, JSchemaType extendedType, String defaultUri, File parent) {
        super(parser, node, extendedType, defaultUri, parent);
    }

    @Override
    public JSchemaType parse() throws JsonGeneratorException {
        JSchemaArrayType type = new JSchemaArrayType();
        setUri(type, this.getRootNode(), this.getParent());
        setType(type, this.getRootNode());
        setExtends(type);
        setRequired(type, this.getRootNode());
        return setItems(type, this.getRootNode(), this.getParent());
    }

    private JSchemaType setItems(JSchemaArrayType type, JsonNode node, File parent) throws JsonGeneratorException {
        JsonNode itemsNode = node.get("items");
        if (itemsNode == null) {
            throw new JsonGeneratorException("The type \"array\" must have the property \"items\".");
        } else {
            TypeBuilder builder = TypeBuilder.getBuilder(this.getParserContext(), itemsNode, null, parent);
            return builder.parse();
        }
    }
}
