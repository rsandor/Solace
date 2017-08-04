'use strict';

Buffs.add('ghost armor', function (buff) {
    buff.init(function (instance, level) {
        instance.setDuration(Buffs.durationByLevel(30, 300, level));
    });

    const armorSlotsNames = ['head', 'body', 'hands', 'waist', 'legs', 'feet'];
    armorSlotsNames.forEach(function (slot) {
        buff.modEquipmentSlotAC(slot, function (instance, character, slot) {
            const currentAc = parseInt(character.getEquipment(slot), 10);
            const effectiveness = ((35.0 / 100.0) + 65.0 * (buffInstance.getLevel() / 100) / 100);
            const armoredAc = effectiveness * Stats.getArmorBaseAC(character.getLevel(), slot);
            return Math.max(currentAc, armoredAc);
        });
    });
});

Buffs.add('truesight', function (buff) {
    buff.onApply(function (b, level) {
        b.setDuration(range(level, 20, 240));
    });

    buff.modPlayerFlags(function (buffInstance, player, flags) {
        flags.set(PlayerFlags.SeeInvisible);
    });
});

Buff.add('impervious', function (buff) {
    buff.setStackable(true);

    buff.init(function (instance, level) {
        instance.setDuration(levelRange(level, 30, 180));
        instance.setStacks(levelRange(level, 1, 4));
    });

    buff.modIncomingDamage(function (instance, player, attacker, damage, type) {
       if (instance.getStacks() < 1 || !type.isMagical()) {
           return damage;
       }
       instance.removeStack();
       return 0;
    });
});

Buff.add('hasted', function (buff) {
    buff.init(function (instance, level) {
        instance.setDuration(levelRange(20, 120, level));
    });

    buff.modNumberOfBaseAttacks(function (player, attacks) {
        return attacks + 1;
    });

    buff.modAC(function (player, ac) {
        return ac * levelRange(level, 1.1, 1.25);
    });
});

Buff.add('frenzied', function (buff) {
    buff.addParameter('weaponType', 'ranged');

    buff.setDisplayName(function () {
        return
    });
});