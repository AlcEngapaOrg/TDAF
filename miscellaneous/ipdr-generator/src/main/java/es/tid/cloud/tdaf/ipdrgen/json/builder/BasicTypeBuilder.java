package es.tid.cloud.tdaf.ipdrgen.json.builder;

import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;

import com.fasterxml.jackson.databind.JsonNode;

import es.tid.cloud.tdaf.ipdrgen.JsonGeneratorException;
import es.tid.cloud.tdaf.ipdrgen.json.JSchemaType;

public class BasicTypeBuilder extends TypeBuilder {
    public BasicTypeBuilder(JSonSchemaParserContext context, JsonNode node, String defaultUri, File parent) {
        super(context, node, defaultUri, parent);
    }
    
    public BasicTypeBuilder(JSonSchemaParserContext parser, JsonNode node, JSchemaType extendedType, String defaultUri, File parent) {
        super(parser, node, extendedType, defaultUri, parent);
    }

    @Override
    public JSchemaType parse() throws JsonGeneratorException {
        JSchemaType type = new JSchemaType();
        setUri(type, this.getRootNode(), this.getParent());
        setType(type, this.getRootNode());
        setRequired(type, this.getRootNode());
        setPattern(type, this.getRootNode(), this.getParent());
        setFormat(type, this.getRootNode(), this.getParent());
        return type;
    }

    protected JSchemaType setPattern(JSchemaType type, JsonNode node, File parent)  throws JsonGeneratorException {
        JsonNode patternNode = node.get("pattern");
        if (patternNode != null) {
            type.setPattern(patternNode.asText());
        }
        return type;
    }

    protected JSchemaType setFormat(JSchemaType type, JsonNode node, File parent)  throws JsonGeneratorException {
        JsonNode formatNode = node.get("format");
        if (formatNode != null) {
            String formatStr = formatNode.asText();
            JSchemaType.Format format = JSchemaType.Format.parse(formatStr);
            if (format == null) {
                String msg = MessageFormat.format("Format \"{0}\" is not valid.", formatStr);
                throw new JsonGeneratorException(msg);
            } else {
                if (!Arrays.asList(format.getValidTypes()).contains(type.getType())) {
                    String msg = MessageFormat.format("Format \"{0}\" cannot be used with the type \"{1}\".", formatStr, type.getType());
                    throw new JsonGeneratorException(msg);
                } else {
                    type.setFormat(format);
                }
            }
        }
        return type;
    }
}
