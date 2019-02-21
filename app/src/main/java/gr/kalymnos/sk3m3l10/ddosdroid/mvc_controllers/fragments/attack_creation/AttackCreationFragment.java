package gr.kalymnos.sk3m3l10.ddosdroid.mvc_controllers.fragments.attack_creation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import gr.kalymnos.sk3m3l10.ddosdroid.R;
import gr.kalymnos.sk3m3l10.ddosdroid.mvc_views.screen_attack_phase.AttackCreationViewMvc;
import gr.kalymnos.sk3m3l10.ddosdroid.mvc_views.screen_attack_phase.AttackCreationViewMvcImpl;
import gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Attack;
import gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Attacks;

public class AttackCreationFragment extends Fragment implements AttackCreationViewMvc.OnNetworkConfigurationSelectedListener,
        AttackCreationViewMvc.OnAttackCreationClickListener, AttackCreationViewMvc.OnWebsiteTextChangeListener {
    private AttackCreationViewMvc viewMvc;
    private OnAttackCreationListener callback;

    public interface OnAttackCreationListener {
        void onAttackCreated(Attack attack);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initViewMvc(inflater, container);
        return viewMvc.getRootView();
    }

    private void initViewMvc(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        viewMvc = new AttackCreationViewMvcImpl(inflater, container);
        viewMvc.setOnAttackCreationClickListener(this);
        viewMvc.setOnNetworkConfigurationSelectedListener(this);
        viewMvc.setOnWebsiteTextChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnAttackCreationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement" + callback.getClass().getCanonicalName());
        }
    }

    @Override
    public void onAttackCreationClicked(String website) {
        if (URLUtil.isValidUrl(website)) {
            Attack attack = createAttack(website);
            callback.onAttackCreated(attack);
        } else {
            Snackbar.make(viewMvc.getRootView(), R.string.enter_valid_url_label, Snackbar.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private Attack createAttack(String website) {
        Attack attack = new Attack(website, viewMvc.getNetworkConf());
        attack.setPushId(Attacks.createPushId());
        return attack;
    }

    @Override
    public void onNetworkSelected(String hint) {
        viewMvc.bindNetworkConfig(hint);
    }

    @Override
    public void websiteTextChanged(String text) {
        if (TextUtils.isEmpty(text)) {
            viewMvc.bindWebsiteCreationTime(getString(R.string.set_the_target_of_the_attack_label));
        } else {
            viewMvc.bindWebsiteCreationTime(getString(R.string.target_set_to_label) + " " + text + ".");
        }
    }
}