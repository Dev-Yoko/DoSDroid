package gr.kalymnos.sk3m3l10.ddosdroid.mvc_model.attack.connectivity.server;

import gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Attack;

import static gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Constants.NetworkType.BLUETOOTH;
import static gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Constants.NetworkType.INTERNET;
import static gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Constants.NetworkType.NSD;
import static gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Constants.NetworkType.WIFI_P2P;

public abstract class Server {
    private static final String TAG = "Server";
    private Attack attack;

    public Server(Attack attack) {
        this.attack = attack;
    }

    public final String getId() {
        return attack.getPushId();
    }

    public interface Builder {
        Server build(Attack attack);
    }

    public static class BuilderImp implements Server.Builder {

        @Override
        public Server build(Attack attack) {
            switch (attack.getNetworkType()) {
                case INTERNET:
                    return new InternetServer(attack);
                case BLUETOOTH:
                    return new BluetoothServer(attack);
                case WIFI_P2P:
                    return new WifiP2pServer(attack);
                case NSD:
                    return new NsdServer(attack);
                default:
                    throw new IllegalArgumentException(TAG + ": Unknown attack network type");
            }
        }
    }
}
