'use strict';

var DecimalFormat = java.text.DecimalFormat;

Commands.add('worth', function (player, params) {
  var dec = new DecimalFormat("#,###,###");
  player.sendln(format(
    "You are currently carrying {y}%s{x} gold.",
    dec.format(player.getGold())
  ));
});
