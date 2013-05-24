package es.tid.cloud.tdaf.ipdrgen.json.builder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.tid.cloud.tdaf.ipdrgen.JsonGeneratorException;
import es.tid.cloud.tdaf.ipdrgen.json.JSchemaType;

public abstract class TypeBuilder {
    private static final Collection<String> PRIMITIVE_TYPE_SET = Arrays.asList(JSchemaType.PRIMITIVE_TYPES);

    private JSonSchemaParserContext parserContext; 
    private JsonNode rootNode;
    private File parent;
    private JSchemaType extendedType = null;
    private String defaultUri = null;

    TypeBuilder(JSonSchemaParserContext context, JsonNode node, String defaultUri, File parent) {
        this.parserContext = context;
        this.rootNode = node;
        this.parent = parent;
        this.defaultUri = defaultUri;
    }

    TypeBuilder(JSonSchemaParserContext context, JsonNode node, JSchemaType extendedType, String defaultUri, File parent) {
        this(context, node, defaultUri, parent);
        this.extendedType = extendedType;
    }

    protected JsonNode getRootNode() {
        return rootNode;
    }

    protected File getParent() {
        return parent;
    }

    protected JSchemaType getExtendedType() {
        return this.extendedType;
    }

    public String getDefaultUri() {
        return defaultUri;
    }

    protected static TypeBuilder getBuilder(JSonSchemaParserContext context, JsonNode node, String defaultUri, File parent) throws JsonGeneratorException {
        TypeBuilder builder = null;
        JsonNode typeNode = node.get("type");
        String type = null;
        if (typeNode != null) {
            type = typeNode.asText();
            if (type.equals("object")) {
                builder = new ObjectTypeBuilder(context, node, defaultUri, parent);
            } else if(type.equals("array")) {
                builder = new ArrayTypeBuilder(context, node, defaultUri, parent);
            } else if(PRIMITIVE_TYPE_SET.contains(type)) {
                builder = new BasicTypeBuilder(context, node, defaultUri, parent);
            }
        } else {
            JsonNode extendsNode = node.get("extends");
            if (extendsNode != null) {
                JSchemaType extendedType = getExtendedType(context, node, parent);
                type = extendedType.getType();
                if (type.equals("object")) {
                    builder = new ObjectTypeBuilder(context, node, extendedType, defaultUri, parent);
                } else if(type.equals("array")) {
                    builder = new ArrayTypeBuilder(context, node, extendedType, defaultUri, parent);
                } else if(PRIMITIVE_TYPE_SET.contains(type)) {
                    builder = new BasicTypeBuilder(context, node, extendedType, defaultUri, parent);
                }
            }
        }
        return builder;
    }

    public static JSchemaType parseJSchema(JSonSchemaParserContext context, File file) throws JsonGeneratorException {
        try {
            JSchemaType type = context.getTypes().get(file.toURI());
            if (type == null) {
                JsonNode rootNode = (new ObjectMapper()).readValue(file, JsonNode.class);
                File parent = file.getParentFile();
                TypeBuilder builder = getBuilder(context, rootNode, file.toURI().toString(), parent);
                type = builder.parse();
            }
            return type;
        } catch (JsonParseException e) {
            throw new JsonGeneratorException(e);
        } catch (JsonMappingException e) {
            throw new JsonGeneratorException(e);
        } catch (JsonGeneratorException e) {
            throw new JsonGeneratorException(e);
        } catch (IOException e) {
            throw new JsonGeneratorException(e);
        }
    }

    public abstract JSchemaType parse() throws JsonGeneratorException;

    protected JSonSchemaParserContext getParserContext() {
        return this.parserContext;
    }

    protected static String resolveUri(String id, File parent) {
        File fileUri = new File(id);
        return fileUri.isAbsolute() ? fileUri.toString() : new File(parent, id).toString();
    }

    protected static JSchemaType getReference(JSonSchemaParserContext context, String ref, File parent) throws JsonGeneratorException {
        String uri = resolveUri(ref, parent);
        JSchemaType typeRef = context.getTypes().get(uri);
        if (typeRef == null) {
            File file = new File(uri);
            if (file.exists()) {
                try {
                    JsonNode rootNode = (new ObjectMapper()).readValue(file, JsonNode.class);
                    TypeBuilder builder = getBuilder(context, rootNode, file.toURI().toString(), file.getParentFile());
                    typeRef = builder.parse();
                } catch (JsonParseException e) {
                    throw new JsonGeneratorException(e);
                } catch (JsonMappingException e) {
                    throw new JsonGeneratorException(e);
                } catch (IOException e) {
                    throw new JsonGeneratorException(e);
                }
            } else {
                throw new JsonGeneratorException("The JSon Schema \"" + uri + "\" not found.");
            }
        }
        return typeRef;
    }

    protected JSchemaType setUri(JSchemaType type, JsonNode rootNode, File parent) {
        JsonNode id = rootNode.get("id");
        String uri;
        if (id != null) {
            uri = resolveUri(id.asText(), parent);
        } else {
            uri = this.defaultUri;
        }
        type.setUri(uri);
        return type;
    }

    protected JSchemaType setType(JSchemaType type, JsonNode rootNode) {
        JsonNode typeNode = rootNode.get("type");
        if (typeNode != null) {
            type.setType(typeNode.asText());
        } else if (this.extendedType != null) {
            type.setType(this.extendedType.getType());
        }
        return type;
    }

    protected static JSchemaType getExtendedType(JSonSchemaParserContext context, JsonNode rootNode, File parent) throws JsonGeneratorException {
        JsonNode extendsNode = rootNode.get("extends");
        if (extendsNode != null) {
            return getReference(context, extendsNode.asText(), parent);
        } else {
            return null;
        }
    }

    protected JSchemaType setExtends(JSchemaType type) {
        if (this.extendedType != null) {
            for(Map.Entry<String, JSchemaType> entry: this.extendedType.getProperties().entrySet()) {
                type.addProperty(entry.getKey(), entry.getValue());
            }
        }
        return type;
    }

    protected JSchemaType setRequired(JSchemaType type, JsonNode rootNode) {
        JsonNode typeNode = rootNode.get("required");
        if (typeNode != null) {
            type.setRequired(typeNode.asBoolean());
        }
        return type;
    }

}
