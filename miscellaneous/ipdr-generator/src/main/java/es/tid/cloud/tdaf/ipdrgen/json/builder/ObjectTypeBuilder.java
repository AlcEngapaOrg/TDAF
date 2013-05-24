package es.tid.cloud.tdaf.ipdrgen.json.builder;

import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.tid.cloud.tdaf.ipdrgen.JsonGeneratorException;
import es.tid.cloud.tdaf.ipdrgen.json.JSchemaType;

public class ObjectTypeBuilder extends TypeBuilder {
    ObjectTypeBuilder(JSonSchemaParserContext parser, JsonNode node, String defaultUri, File parent) throws JsonGeneratorException {
        super(parser, node, defaultUri, parent);
    }

    ObjectTypeBuilder(JSonSchemaParserContext parser, JsonNode node, JSchemaType extendedType, String defaultUri, File parent) throws JsonGeneratorException {
        super(parser, node, extendedType, defaultUri, parent);
    }

    public JSchemaType parse() throws JsonGeneratorException {
        JSchemaType type = null;
        type = new JSchemaType();
        setUri(type, this.getRootNode(), this.getParent());
        setType(type, this.getRootNode());
        setExtends(type);
        setRequired(type, this.getRootNode());
        setProperties(type, this.getRootNode(), this.getParent());

        this.getParserContext().getTypes().put(type.getUri(), type);
        return type;
    }


    protected JSchemaType setProperties(JSchemaType type, JsonNode rootNode, File parent) throws JsonGeneratorException {
        JsonNode propsNode = rootNode.get("properties");
        if (propsNode != null) {
            ObjectNode propContainer = (ObjectNode) propsNode;
            for(Iterator<Map.Entry<String, JsonNode>> propIt = propContainer.fields(); propIt.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = propIt.next();
                JSchemaType property = getProperty(entry.getValue(), parent);
                String propertyName = entry.getKey();
                if (property == null) {
                    String msg = MessageFormat.format("The property \"{0}\" is not recognized.", propertyName);
                    throw new JsonGeneratorException(msg);
                }
                type.addProperty(propertyName, property);
            }
        }
        return type;
    }

    protected JSchemaType getProperty(JsonNode node, File parent) throws JsonGeneratorException {
        JsonNode nodeRef = node.get("$ref");
        if (nodeRef == null) {
            TypeBuilder builder = TypeBuilder.getBuilder(this.getParserContext(), node, null, parent);
            if (builder != null) {
                return builder.parse();
            } else {
                return null;
            }
        } else {
            return getReference(this.getParserContext(), nodeRef.asText(), parent);
        }
    }
}
