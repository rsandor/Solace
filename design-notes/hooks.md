# Hooks
Listed below is the design for all types of hooks needed from the perspective of
a script author.


## Listing of All Scripting Hooks

### Resource Statistics and Recovery Modification
- [ ] Engine Support
```js
{
    modMaxHp: function(player, maxHp) {
        return 0.75 * maxHp;
    },
    modMaxMp: function (player, maxMp) {
        return 0;
    },
    modMaxSp: function (player, maxSp) {
        return maxSp / 2.0;
    },
    modHpRecovery: function (player, hpToRecover) {
        return 1.25 * hpToRecover;
    },
    modMpRecovery: function (player, mpToRecover) {
        return 0.85 * mpToRecover;
    },
    modSpRecovery: function (player, spToRecover) {
        return 0;
    },
    modHpRecoveryPerBattleRound: function (player, hpToRecover) {
        return: 0;
    },
    modSpRecoveryPerBattleRound: function (player, spToRecover) {
        return: 0;
    },
    modMpRecoveryPerBattleRound: function (player, mpToRecover) {
        return: 0;
    },
    modHpCost: function (player, hpCost) {
        return 0.75*hpCost;
    },
    modMpCost: function (player, mpCost) {
        return 0.75*mpCost;
    },
    modSpCost: function (player, spCost) {
        return 0.75*spCost;
    }
}
```

### Armor Class Modification
- [ ] Engine Support
```js
{
    modAC: function (player, totalAc) {
        return 0.85 * totalAC;
    },
    modEquipmentSlotAc: {
        slot: 'head',
        mod: function (player, slotAc) {
            return 1.25 * slotAc;
        }
    }
}
```

### Modify Ability Score
- [ ] Engine Support
```js
{
    modStr: function (player, str) { return 10; },
    modMag: function (player, mag) { return mag / 2; },
    modVit: function (player, vit) { return 1.1 * vit; },
    modSpe: function (player, spe) { return 0; }
}
```

### Modify Base Attacks
- [ ] Engine Support
```js
{
    modBaseAttackPotency: function (player, potency) {
        if (player.getWeaponType() === 'ranged') {
            return potency + 25;
        }
        return potency;
    },
    modBaseAttackDamage: function (player, damage, type) {
        if (type.is('slashing')) {
            return 1.25 * damage;
        }
    },
    modBaseAttackRoll: function (player, roll) {
        return 0.5 * roll;
    },
    modNumberOfBaseAttacks: function (player, attacks) {
        if (player.getWeapon().isTwoHanded()) {
            return Math.max(2, attacks);
        }
        return attacks;
    },
    onBaseAttackHit: function (player, target) {
        return;
    },
    onBaseAttackMiss: function (player, target) {
        return;
    }
}
```

### Set Player Flags
- [ ] Engine Support
```js
{
    modPlayerFlags: function (flags) {
        flags.set(PlayerFlags.SeeInvisible);
    }
}
```

### Modify Damage
- [ ] Engine Support
```js
{
    // NOTE: Going to need to handle infinite reflections here...
    //       (see block's reflect passive)
    // NOTE: We should support negative damage numbers as well
    //       to turn an attack into healing (see heavy armor's steadfast passive)
    modIncomingDamage: function (player, attacker, damage, type) {
        if (!type.isMagical()) {
            return damage;
        }
        player.getBuff('impervious').removeStack();
        return 0;
    },
    modOutgoingDamage: function (player, target, damage, type) {
        if (type.isPhysical()) {
            return 1.1 * damage;
        }
        return damage;
    },
    modIncomingDamageType: function (player, attacker, damage, type) {
        return DamageTypes.get('bludgeoning');
    },
    modOutgoingDamageType: function (player, target, damage, type) {
        return (type.isMagical()) ? type : DamageTypes.get('radiant');
    }
}
```

### Modify Damage Resistances, Vulnerabilities, and Immunities
- [ ] Engine Support
```js
{
    modDamageResistance: {
        damageType: 'bludgeoning',
        mod: function (player, resistance) {
            return resistance + 25;
        }
    },
    modDamageVulnerability: {
        damageType: 'fire',
        mod: function (player, vulnerability) {
            return vulnerability + 30;
        }
    },
    modDamageImmunity: {
        damageType: 'radiant',
        mod: function (player, isImmune) {
            return true;
        }
    }
}
```

### Modify Buffs
- [ ] Engine Support
```js
{
    modBuffResistance: {
        buffName: 'stun',
        mod: function (player, resistance) {
            return resistance + 25;
        }
    },
    modBuffDuration: {
        buffName: 'shielded',
        mod: function (player, duration) {
            return duration * 2;
        }
    },
    modBuffDuration: function (player, duration) {
        return duration * 1.33;
    },
    modDebuffDuration: function (player, duration) {
        return duration * 0.66;
    }
}
```

### Cooldown Success/Failure Override
- [ ] Engine Support
```js
{
    modCooldownSuccess: function (player, cooldown) {
        if (cooldown.getCastTime() === 0) {
            return true;
        }
        return Roll.uniform() < 0.5;
    },
    modCooldownRetry: function (player, cooldown) {
        return Roll.uniform() < 0.15;
    },
    modCastTime: function (player, cooldown, castTime) {
        return castTime * 0.5;
    },
    modCastInterrupted: function (player) {
        return false;
    }
}
```

### Pet Modification
- [ ] Engine Support
```js
{
    modPetAc: function (player, pet, ac) {
        return 0.25 * ac;
    },
    modPetNumberOfAttacks: function (player, pet, attacks) {
        return Math.min(2, attacks);
    },
    modPetAttackRoll: function (player, pet, roll) {
        return 1.1 * roll;
    },
    modPetAttackPotency: function (player, pet, potency) {
        return 0.5 * potency;
    }
}
```

### Perform action on cycle of ticks
- [ ] Engine Support
```js
{
    setDamageOverTime: {
        period: 2,
        damage: function (player) {
            const scalar = player.getSkillLevel('necromancy') / 100.0;
            return 10 + 25 * scalar;
        }
    }
    onIncomingDamageOverTime: function (player, source, damage) {
        return;
    },
    onOutgoingDamageOverTime: function (player, source, target, damage) {
        return;
    },
    modIncomingDamageOverTime: function (player, attacker, source, damage) {
        return 0.5 * damage;
    },
    modOutgoingDamageOverTime: function (player, target, source, damage) {
        return 1.5 * damage;
    },
    onPeriod: {
        ticks: 2,
        action: function (player) {
            player.sendln('Your target is north.');
        }
    }
}
```

### Traps
- [ ] Engine Support
```js
{
    onTrapEncountered: function (player, trap) {
        return;
    }
}
```

### General
- [ ] Engine Support
```js
{
    modStoreBuyPrice: function (player, price) {
        return 0.75 * price;
    },
    modStoreSellPrice: function (player, price) {
        return 1.25 * price;
    },
    modMobileAggression: function () {
        // TODO Need to understand how aggression works first...
    }
}
```
