package net.hyze.skyblock.framework.api.utils;

import net.hyze.core.shared.world.location.SerializedLocation;
import net.hyze.core.shared.world.materialdata.SerializedMaterialData;
import org.bson.Document;

public class DocumentUtils {

    public static SerializedMaterialData getMaterialData(Document document, String key) {
        if (document.containsKey(key)) {
            String value = document.getString(key);
            return SerializedMaterialData.of(value);
        }

        return new SerializedMaterialData("BARRIER");
    }

    /*

     */

    public static SerializedLocation getSerializedLocation(Document document, String key) {
        Document location = (Document) document.get(key);
        return new SerializedLocation("world",
            location.getDouble("x"),
            location.getDouble("y"),
            location.getDouble("z"),
            location.getDouble("yaw").floatValue(),
            location.getDouble("pitch").floatValue()
        );
    }

    public static Document getLocationDocument(SerializedLocation spawnLocation) {
        Document document = new Document();
        document.put("x", spawnLocation.getX());
        document.put("y", spawnLocation.getY());
        document.put("z", spawnLocation.getZ());
        document.put("yaw", spawnLocation.getYaw());
        document.put("pitch", spawnLocation.getPitch());
        return document;
    }
}
