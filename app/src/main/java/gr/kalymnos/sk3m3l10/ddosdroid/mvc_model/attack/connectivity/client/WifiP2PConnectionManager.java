package gr.kalymnos.sk3m3l10.ddosdroid.mvc_model.attack.connectivity.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;

import gr.kalymnos.sk3m3l10.ddosdroid.mvc_model.attack.connectivity.network_constraints.NetworkConstraintsResolver;
import gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Attack;

class WifiP2PConnectionManager extends ConnectionManager implements NetworkConstraintsResolver.OnConstraintsResolveListener {
    private static final String TAG = "WifiP2PConnectionManage";

    private NetworkConstraintsResolver constraintsResolver;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager wifiP2pManager;
    private BroadcastReceiver wifiDirectReceiver;

    WifiP2PConnectionManager(Context context, Attack attack) {
        super(context, attack);
        initializeFields(context, attack);
    }

    private void initializeFields(Context context, Attack attack) {
        wifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        initializeConstraintsResolver(context, attack);
        initializeWifiDirectReceiver();
    }

    private void initializeConstraintsResolver(Context context, Attack attack) {
        NetworkConstraintsResolver.Builder builder = new NetworkConstraintsResolver.BuilderImp();
        constraintsResolver = builder.build(context, attack.getNetworkType());
        constraintsResolver.setOnConstraintsResolveListener(this);
    }

    private void initializeWifiDirectReceiver() {
        wifiDirectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                        //  Check to see if wifi is enabled/disabled
                        break;
                    case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                        //  Call WifiP2pManager.requestPeers() to get a list of current peers
                        break;
                    case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                        //  Respond to new connection or disconnections
                        break;
                    case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                        //  Respond to this device's wifi state changing
                        break;
                    default:
                        throw new IllegalArgumentException(TAG + ": Unknown action");
                }
            }
        };
    }

    @Override
    void connect() {
        constraintsResolver.resolveConstraints();
    }

    @Override
    void disconnect() {
        client.onManagerDisconnection();
        releaseResources();
    }

    @Override
    protected void releaseResources() {
        constraintsResolver.releaseResources();
        super.releaseResources();
    }

    @Override
    public void onConstraintsResolved() {
        channel = wifiP2pManager.initialize(context, Looper.getMainLooper(), null);
    }

    @Override
    public void onConstraintResolveFailure() {
        client.onManagerError();
        releaseResources();
    }
}
