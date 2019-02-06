package gr.kalymnos.sk3m3l10.ddosdroid.mvc_controllers.fragments.attack_list;

import gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Attack;

import static gr.kalymnos.sk3m3l10.ddosdroid.pojos.attack.Constants.NetworkType.NSD;

public class NSDAttackListFragment extends AttackListFragment {
    private static final String TAG = AttackListFragment.TAG + "NSD";

    @Override
    public void onAttackUpload(Attack attack) {
        if (attack.getNetworkType() == NSD) {
            cacheAttackAndBind(attack);
        }
    }

    @Override
    public void onAttackUpdate(Attack changedAttack) {
        if (changedAttack.getNetworkType() == NSD) {
            deleteFromCacheAttackWith(changedAttack.getPushId());
            cacheAttackAndBind(changedAttack);
        }
    }

    @Override
    public void onAttackDelete(Attack deletedAttack) {
        if (deletedAttack.getNetworkType() == NSD) {
            deleteFromCacheAttackWith(deletedAttack.getPushId());
            bindAttacks();
        }
    }
}
