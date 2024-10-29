package net.hyze.skyblock.framework.plugin.utils;

import com.google.common.collect.Lists;
import java.util.List;
import net.hyze.core.shared.misc.utils.Printer;
import net.hyze.skyblock.framework.api.SkyBlockApiProvider;
import net.hyze.skyblock.framework.api.user.attributes.AttributeData;
import net.hyze.skyblock.framework.api.user.attributes.AttributeRegistry;
import net.hyze.skyblock.framework.api.user.attributes.api.AbstractAttribute;
import net.hyze.skyblock.framework.api.user.attributes.api.BasicAttribute;
import net.hyze.skyblock.framework.api.user.profile.Profile;

public class AttributeUtils {

    public static void manageAttributes(Profile profile) {
        AttributeData attributeData = SkyBlockApiProvider.Cache.Local.USERS_ATTRIBUTES.provide().get(profile);
        if (attributeData == null) {
            attributeData = AttributeData.empty();
        }

        List<BasicAttribute> attributes = Lists.newLinkedList();
        for (AbstractAttribute attribute : AttributeRegistry.getAttributes()) {
            if (!(attribute instanceof BasicAttribute)) {
                continue;
            }

            BasicAttribute basicAttribute = (BasicAttribute) attribute;
            if (attributeData.contains(basicAttribute)) {
                continue;
            }

            attributes.add(basicAttribute);
        }

        if (!attributes.isEmpty()) {
            Printer.INFO.print("Inserting default attributes");
            SkyBlockApiProvider.Repositories.SKYBLOCK_USERS.provide().profiles().attributes().updateBase(profile, attributeData, attributes);
        }
    }

}
