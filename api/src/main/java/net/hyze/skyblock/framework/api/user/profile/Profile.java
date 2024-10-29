package net.hyze.skyblock.framework.api.user.profile;

import com.google.common.base.Enums;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import net.hyze.core.shared.apps.AppType;
import net.hyze.core.shared.world.materialdata.SerializedMaterialData;
import net.hyze.skyblock.framework.api.utils.DocumentUtils;
import org.bson.Document;
import org.bson.types.ObjectId;

@EqualsAndHashCode(of = "id")
@AllArgsConstructor
public class Profile {

    @Getter
    private ObjectId id;

    @Getter
    private String name;

    @Getter
    private SerializedMaterialData icon;

    @Getter
    private String islandId;

    @Getter
    private double purse;

    @Getter
    @Setter
    private AppType lastApp;

    /*

     */
    public Profile(Document document) {
        this.id = document.getObjectId("_id");
        this.name = document.getString("name");
        this.icon = DocumentUtils.getMaterialData(document, "icon");
        this.islandId = document.getString("island_id");
        this.purse = document.get("purse", 0.0);
        this.lastApp = Enums.getIfPresent(AppType.class, document.get("last_app", "SKYBLOCK_ISLANDS")).or(AppType.SKYBLOCK_ISLANDS);
    }

    /*

     */
    public static Profile empty(String islandId) {
        return new Profile(new ObjectId(), "Novo perfil", new SerializedMaterialData("GRASS"), islandId, 0.0, AppType.SKYBLOCK_ISLANDS);
    }

    public void addPurse(double value) {
        this.purse += value;
    }
}
