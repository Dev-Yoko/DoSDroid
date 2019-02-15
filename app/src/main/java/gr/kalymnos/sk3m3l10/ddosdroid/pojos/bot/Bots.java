package gr.kalymnos.sk3m3l10.ddosdroid.pojos.bot;

import gr.kalymnos.sk3m3l10.ddosdroid.mvc_model.instance_id.FirebaseInstanceId;

public final class Bots {
    public static Bot local() {
        return new Bot(new FirebaseInstanceId().getInstanceId());
    }

    public static String getLocalUserId() {
        return local().getId();
    }
}
