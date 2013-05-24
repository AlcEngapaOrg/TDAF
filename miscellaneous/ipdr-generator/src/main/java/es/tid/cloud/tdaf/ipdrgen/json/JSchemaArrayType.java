package es.tid.cloud.tdaf.ipdrgen.json;

public class JSchemaArrayType extends JSchemaType {
    private JSchemaType items = null;

    public JSchemaType getItems() {
        return items;
    }

    public void setItems(JSchemaType items) {
        this.items = items;
    }
}
