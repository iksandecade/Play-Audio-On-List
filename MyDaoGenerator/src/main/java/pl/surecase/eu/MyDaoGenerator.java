package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "project.iksandecade.playaudioonlist.dao");

        addList(schema);

        new DaoGenerator().generateAll(schema, args[0]);
    }


    private static void addList(Schema schema) {
        Entity entity = schema.addEntity("ListAudio");
        entity.setHasKeepSections(true);
        entity.setTableName("ListAudio");
        entity.addLongProperty("id").primaryKey().autoincrement();
        entity.addStringProperty("name");
        entity.addLongProperty("timeStamp");
    }
}
