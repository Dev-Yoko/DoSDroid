package gr.kalymnos.sk3m3l10.ddosdroid.mvc_controllers.fragments.attack_list;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import gr.kalymnos.sk3m3l10.ddosdroid.R;
import gr.kalymnos.sk3m3l10.ddosdroid.mvc_controllers.activities.JoinAttackActivity;
import gr.kalymnos.sk3m3l10.ddosdroid.mvc_model.attack.connectivity.client.ClientHost;
import gr.kalymnos.sk3m3l10.ddosdroid.mvc_model.attack.connectivity.server.ServerHost;
import gr.kalymnos.sk3m3l10.ddosdroid.mvc_model.attack.repository.AttackRepository;
import gr.kalymnos.sk3m3l10.ddosdroid.mvc_model.attack.repository.FirebaseRepository;
import gr.kalymnos.sk3m3l10.ddosdroid.mvc_views.screen_attack_lists.AttackListViewMvc;
import gr.kalymnos.sk3m3l10.ddosdroid.mvc_views.screen_attack_lists.AttackListViewMvcImpl;
import gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Attack;
import gr.kalymnos.sk3m3l10.ddosdroid.pojos.bot.Bots;

import static gr.kalymnos.sk3m3l10.ddosdroid.constants.ContentTypes.FETCH_ONLY_USER_JOINED_ATTACKS;
import static gr.kalymnos.sk3m3l10.ddosdroid.constants.ContentTypes.FETCH_ONLY_USER_NOT_JOINED_ATTACKS;
import static gr.kalymnos.sk3m3l10.ddosdroid.constants.ContentTypes.FETCH_ONLY_USER_OWN_ATTACKS;
import static gr.kalymnos.sk3m3l10.ddosdroid.constants.ContentTypes.INVALID_CONTENT_TYPE;
import static gr.kalymnos.sk3m3l10.ddosdroid.constants.Extras.EXTRA_ATTACKS;
import static gr.kalymnos.sk3m3l10.ddosdroid.constants.Extras.EXTRA_CONTENT_TYPE;
import static gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Attacks.includesBot;
import static gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Attacks.isAttackOwnedByBot;
import static gr.kalymnos.sk3m3l10.ddosdroid.utils.BundleUtil.containsKey;
import static gr.kalymnos.sk3m3l10.ddosdroid.utils.CollectionUtil.getItemFromLinkedHashSet;
import static gr.kalymnos.sk3m3l10.ddosdroid.utils.CollectionUtil.hasItems;

public abstract class AttackListFragment extends Fragment implements AttackListViewMvc.OnAttackClickListener,
        AttackListViewMvc.OnJoinSwitchCheckedStateListener, AttackListViewMvc.OnActivateSwitchCheckedStateListener,
        AttackRepository.OnRepositoryChangeListener {
    protected static final String TAG = "AttackListFrag";

    protected AttackListViewMvc viewMvc;
    private AttackRepository repository;
    protected LinkedHashSet<Attack> cachedAttacks;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeFieldsExceptViewMvc();
        repository.startListenForChanges();
    }

    private void initializeFieldsExceptViewMvc() {
        cachedAttacks = new LinkedHashSet<>();
        initializeRepository();
    }

    private void initializeRepository() {
        repository = new FirebaseRepository();
        repository.addOnRepositoryChangeListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializeViewMvc(inflater, container);
        return viewMvc.getRootView();
    }

    private void initializeViewMvc(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        viewMvc = new AttackListViewMvcImpl(inflater, container);
        viewMvc.setOnAttackClickListener(this);
        viewMvc.setOnJoinSwitchCheckedStateListener(this);
        viewMvc.setOnActivateSwitchCheckedStateListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<Attack> savedAttacks = getAttacksFrom(savedInstanceState);
        if (hasItems(savedAttacks)) {
            cacheAndDisplay(savedAttacks);
        }
    }

    private List<Attack> getAttacksFrom(Bundle savedInstanceState) {
        if (containsKey(savedInstanceState, EXTRA_ATTACKS)) {
            return getAttacksFrom(savedInstanceState);
        }
        return null;
    }

    private void cacheAndDisplay(List<Attack> savedAttacks) {
        cachedAttacks.clear();
        cachedAttacks.addAll(savedAttacks);
        viewMvc.bindAttacks(cachedAttacks);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        repository.stopListenForChanges();
    }

    @Override
    public final void onSaveInstanceState(@NonNull Bundle outState) {
        if (hasItems(cachedAttacks)) {
            List<Attack> attacksCopy = new ArrayList<>(cachedAttacks);
            outState.putParcelableArrayList(EXTRA_ATTACKS, (ArrayList<? extends Parcelable>) attacksCopy);
        }
    }

    @Override
    public void onAttackClick(int position) {
        if (getContentType() == FETCH_ONLY_USER_NOT_JOINED_ATTACKS) {
            Attack attack = getItemFromLinkedHashSet(cachedAttacks, position);
            JoinAttackActivity.startAnInstance(getContext(), attack);
        }
    }

    protected final int getContentType() {
        if (containsKey(getArguments(), EXTRA_CONTENT_TYPE)) {
            return getArguments().getInt(EXTRA_CONTENT_TYPE);
        }
        return INVALID_CONTENT_TYPE;
    }

    @Override
    public void onJoinSwitchCheckedState(int position, boolean isChecked) {
        if (!isChecked) {
            Attack attack = getItemFromLinkedHashSet(cachedAttacks, position);
            ClientHost.Action.stopClientOf(attack, getContext());
            Snackbar.make(viewMvc.getRootView(), getString(R.string.not_following_attack) + " " + attack.getWebsite(), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivateSwitchCheckedState(int position, boolean isChecked) {
        if (!isChecked) {
            Attack attack = getItemFromLinkedHashSet(cachedAttacks, position);
            ServerHost.Action.stopServerOf(attack.getWebsite(),getContext());
            Snackbar.make(viewMvc.getRootView(), getString(R.string.canceled_attack) + " " + attack.getWebsite(), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public final void onAttackDelete(Attack deletedAttack) {
        deleteFromCacheAttackWith(deletedAttack.getPushId());
        viewMvc.bindAttacks(cachedAttacks);
    }

    protected final void deleteFromCacheAttackWith(String attackId) {
        for (Iterator<Attack> iterator = cachedAttacks.iterator(); iterator.hasNext(); ) {
            boolean foundAttack = iterator.next().getPushId().equals(attackId);
            if (foundAttack) {
                iterator.remove();
                return;
            }
        }
    }

    protected final void cacheAttackAccordingToContentType(Attack attack) {
        if (getContentType() == FETCH_ONLY_USER_JOINED_ATTACKS) {
            if (includesBot(attack, Bots.local())) {
                cachedAttacks.add(attack);
            }
        } else if (getContentType() == FETCH_ONLY_USER_NOT_JOINED_ATTACKS) {
            boolean attackNotJoinedOrOwnedByUser = !includesBot(attack, Bots.local()) && !isAttackOwnedByBot(attack, Bots.local());
            if (attackNotJoinedOrOwnedByUser) {
                cachedAttacks.add(attack);
            }
        } else if (getContentType() == FETCH_ONLY_USER_OWN_ATTACKS) {
            boolean userOwnsThisAttack = isAttackOwnedByBot(attack, Bots.local()) && !includesBot(attack, Bots.local());
            if (userOwnsThisAttack) {
                cachedAttacks.add(attack);
            }
        }
    }

    /*
     * Baring down a switch statement. Technique used to clean the code. Justification lies in
     * Uncle Bob's "Clean Code", chapter 3, page 39.
     *
     * */

    public interface Builder {
        AttackListFragment build(String tabTitle, int contentType);
    }

    public static class BuilderImp implements Builder {

        @Override
        public AttackListFragment build(String tabTitle, int contentType) {
            AttackListFragment instance = getAttackListFragmentImplFromTabTitle(tabTitle);
            instance.setArguments(createFragmentArgs(contentType));
            return instance;
        }

        private AttackListFragment getAttackListFragmentImplFromTabTitle(String tabTitle) {
            switch (tabTitle) {
                // Titles were copied from R.arrays.network_technologies_titles
                case "Internet":
                    return new InternetAttackListFragment();
                case "WiFi P2P":
                    return new WiFiP2PAttackListFragment();
                case "NSD":
                    return new NSDAttackListFragment();
                case "Bluetooth":
                    return new BluetoothAttackListFragment();
                default:
                    throw new UnsupportedOperationException(TAG + " " + tabTitle + " is not a valid tab title");
            }
        }

        @NonNull
        protected static Bundle createFragmentArgs(int contentType) {
            Bundle args = new Bundle();
            args.putInt(EXTRA_CONTENT_TYPE, contentType);
            return args;
        }
    }
}
