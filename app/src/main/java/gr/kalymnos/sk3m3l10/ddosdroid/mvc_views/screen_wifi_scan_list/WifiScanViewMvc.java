package gr.kalymnos.sk3m3l10.ddosdroid.mvc_views.screen_wifi_scan_list;

import java.util.List;

import gr.kalymnos.sk3m3l10.ddosdroid.mvc_views.ViewMvcWithToolbar;

public interface WifiScanViewMvc extends ViewMvcWithToolbar {

    public interface OnWifiScanItemClickListener {
        void onWifiScanItemClick(int position);
    }

    void bindResults(List<String> wifiScanResultTitles);

    void setOnWifiScanItemClickListener(OnWifiScanItemClickListener listener);
}