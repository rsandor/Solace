@name(shops)
@author(Ryan Sandor Richards)

# Shops
Shop are scattered throughout the game world and allow the player to exchange
currency (such as gold) for items and equipment. For the most part, it should
be relatively obvious from the room description when you have entered a shop.

Some shops carry an unlimited number of a single item, meaning the player can
buy as many as they wish from the vendor. Other shops have limited quantities
of items that periodically restock.

## Listing What's For Sale
Players can see a listing of items for sale in a shop by using the `list`
command. The command will display a list of the items detailing the name,
inventory number, and price of the item.

## Buying
Upon deciding to buy an item given by the `list` command, players can use the
`buy` [name | number] command to buy an item with the given name or inventory
number (displayed to the left of the item's name in the `list`).

If the character has enough of the currency required then the exchange will be
made. The character will find the new item in their inventory and an amount of
gold equal to the sale price missing from their coffers.

## Appraisals
To determine the value of the items in a character's inventory, player can use
the `appraise` [name] command. The shop owner will then let the player know how
much gold they are willing to part with when buying the item. Different shop
owners will buy the same item at different prices, so it may be prudent to
"shop around" before deciding to sell an item.

## Selling
When determined to do so, players can sell items in their character's inventory
by using the `sell` [name] command. The item will be removed from the
character's inventory and an amount of gold as detailed by the `appraise`
command will be added to the character's coffers.

See also: `help list`, `help buy`, `help appraise`, `help sell`
