## Entity 'types':

Search tx/spawn-entity:

tx/spawn-creature
    => npc/player

tx/spawn-effect
    => tx/audiovisual
    => tx/spawn-alert
    => tx/spawn-line

tx/spawn-item
tx/spawn-projectile


## general
:moon.content-grid/content-cell
:moon.create.grid/touched-cells
:moon.create.grid/occupied-cells

entity/id
:entity/mouseover?
:entity/clickable
:entity/line-render
:entity/temp-modifier
:entity/string-effect

:active-skill
:npc-dead
:npc-idle
:npc-moving
:npc-sleeping
:player-dead
:player-idle
:player-item-on-cursor
:player-moving
:stunned

entity/alert-friendlies-after-duration
entity/delete-after-duration
entity/destroy-audiovisual
entity/movement
entity/projectile-collision

:entity/destroyed?

## Creatures

:property/id
:property/pretty-name
:entity/fsm
:entity/faction
:entity/player?
    * set-item callback
    * remove-item callback
    * add skill (remoove skill) callback
    * affected targets w/o player (can make w/o effect id ? )
:entity/free-skill-points
:entity/clickable
:entity/click-distance-tiles
:entity/image
:entity/animation
:property/pretty-name
:entity/species
:creature/level
:entity/body
:entity/stats
:entity/inventory
:entity/skills

:entity/item-on-cursor (player only? ) - an extra type ...

## Projectile
:property/id
:entity/image
:projectile/max-range
:projectile/speed
:projectile/piercing?
:projectile/size
:entity-effects

entity/body
entity/movement
entity/delete-after-duration
entity/destroy-audiovisual
entity/projectile-collision

## Item
:entity/item
