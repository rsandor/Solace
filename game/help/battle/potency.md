@name(potency)
@author(Ryan Sandor Richards)

# Battle Mechanic: Potency
Potency is an attribute of an attack that roughly determines the power of the
attack. For any given attack, the potency of the attack is used to calculate
the resulting damage as a relative percentage of the player's base weapon attack
or the base magic attack (depending on the type of attack).

For example, if a player has an average base weapon attack damage of 80 then a
cooldown with potency of 100 will also have an average damage of 80. An attack
with potency of 50 will have exactly 50% of the power and will have an average
damage of 40. And finally, an attack with potency 1800 will have a average
damage 18 times that of the base yielding an average damage of 1440.

The potency of an attack is a general metric that one can use when judging its
overall power. However, it cannot be used as the only metric since one should
also factor in resource costs (`hp`, `sp`, `mp`), buffs, debuffs, and side
effects (such as healing, etc.).
