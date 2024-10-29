package net.hyze.skyblock.framework.api.user.attributes;

import com.google.common.collect.Maps;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.hyze.skyblock.framework.api.user.attributes.api.AbstractAttribute;
import net.hyze.skyblock.framework.api.user.attributes.api.BasicAttribute;
import net.hyze.skyblock.framework.api.user.attributes.api.ComputedAttribute;
import org.bson.Document;

@NoArgsConstructor(staticName = "empty")
public class AttributeData {

    @Getter
    private Map<BasicAttribute, Object> attributes = Maps.newHashMap();

    /*

     */

    public boolean contains(BasicAttribute attribute) {
        return attributes.containsKey(attribute);
    }

    public <V> V getValue(AbstractAttribute<V> attribute) {
        if(attribute instanceof BasicAttribute) {
            BasicAttribute<V> basicAttribute = (BasicAttribute<V>) attribute;
            return (V) this.attributes.getOrDefault(basicAttribute, basicAttribute.getDefaultValue());
        }

        ComputedAttribute<V> computedAttribute = (ComputedAttribute<V>) attribute;
        return computedAttribute.getValue(this);
    }

    public AttributeData(Document attributesDocument) {
        for(String key : attributesDocument.keySet()) {
            AbstractAttribute attribute = AttributeRegistry.getAttribute(key);

            if(attribute == null || !(attribute instanceof BasicAttribute)) {
                continue;
            }

            Document document = attributesDocument.get(key, Document.class);
            if(!document.containsKey("base")) {
                continue;
            }

            this.attributes.put((BasicAttribute) attribute, document.get("base"));
        }
    }

}
