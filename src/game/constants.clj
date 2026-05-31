; can be loaded by edn .... through bindRoot
; state in defs _is_ advantegeous...
; or is it ?
(ns game.constants
  (:require [reduce-fsm :as fsm]))

(comment

 ; 1. quote the data structur ebecause of arrows
 ; 2.
 (eval `(fsm/fsm-inc ~data))
 )

(def npc-fsm
  (fsm/fsm-inc
   [[:npc-sleeping
     :kill -> :npc-dead
     :stun -> :stunned
     :alert -> :npc-idle]
    [:npc-idle
     :kill -> :npc-dead
     :stun -> :stunned
     :start-action -> :active-skill
     :movement-direction -> :npc-moving]
    [:npc-moving
     :kill -> :npc-dead
     :stun -> :stunned
     :timer-finished -> :npc-idle]
    [:active-skill
     :kill -> :npc-dead
     :stun -> :stunned
     :action-done -> :npc-idle]
    [:stunned
     :kill -> :npc-dead
     :effect-wears-off -> :npc-idle]
    [:npc-dead]]))

(def player-fsm
  (fsm/fsm-inc
   [[:player-idle
     :kill -> :player-dead
     :stun -> :stunned
     :start-action -> :active-skill
     :pickup-item -> :player-item-on-cursor
     :movement-input -> :player-moving]
    [:player-moving
     :kill -> :player-dead
     :stun -> :stunned
     :no-movement-input -> :player-idle]
    [:active-skill
     :kill -> :player-dead
     :stun -> :stunned
     :action-done -> :player-idle]
    [:stunned
     :kill -> :player-dead
     :effect-wears-off -> :player-idle]
    [:player-item-on-cursor
     :kill -> :player-dead
     :stun -> :stunned
     :drop-item -> :player-idle
     :dropped-item -> :player-idle]
    [:player-dead]]))

(def black [0 0 0 1])
(def white [1 1 1 1])
(def gray  [0.5 0.5 0.5 1])
(def red   [1 0 0 1])

(def outline-alpha 0.4)

(def world-fn-file
  "config/world_fns/modules.edn"
  ; "config/world_fns/vampire.edn"
  ; "config/world_fns/uf_caves.edn"
  )

(def spiderweb-modifiers {:modifier/movement-speed {:op/mult -50}})
(def spiderweb-duration 5)

(def skill-image-radius-world-units
  (let [tile-size 48
        image-width 32]
    (/ (/ image-width tile-size) 2)))

(def mouseover-ellipse-width 5)

(def reaction-time-multiplier 0.016)

(def zoom-speed 0.025)

(def pausing? true)

(declare txs-fn-map)

(declare draw-fns)

(def k->colors
  {:property/pretty-name "PRETTY_NAME"
   :stats/modifiers "CYAN"
   :maxrange "LIGHT_GRAY"
   :creature/level "GRAY"
   :projectile/piercing? "LIME"
   :skill/action-time-modifier-key "VIOLET"
   :skill/action-time "GOLD"
   :skill/cooldown "SKY"
   :skill/cost "CYAN"
   :entity/delete-after-duration "LIGHT_GRAY"
   :entity/faction "SLATE"
   :entity/fsm "YELLOW"
   :entity/species "LIGHT_GRAY"
   :entity/temp-modifier "LIGHT_GRAY"})

(def k-order
  [:property/pretty-name
   :skill/action-time-modifier-key
   :skill/action-time
   :skill/cooldown
   :skill/cost
   :skill/effects
   :entity/species
   :creature/level
   :entity/stats
   :entity/delete-after-duration
   :projectile/piercing?
   :entity/projectile-collision
   :maxrange
   :entity-effects])
