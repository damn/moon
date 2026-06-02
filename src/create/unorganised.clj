(ns create.unorganised
  (:require [clojure.core.edn-resource :refer [edn-resource]]
            [clojure.gdx.graphics.color :refer [Color]]
            [clojure.gdx.graphics.colors :as colors]
            [malli.core :as m]
            [reduce-fsm :as fsm]))

(comment

 ; 1. quote the data structur ebecause of arrows
 ; 2.
 (eval `(fsm/fsm-inc ~data))
 )

(defn step [ctx]
  (doseq [[name rgba] {"PRETTY_NAME" [0.84 0.8 0.52 1]}]
    (colors/put! name (Color rgba)))
  (assoc ctx
         :ctx/fsms {
                    :npc (fsm/fsm-inc
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
                           [:npc-dead]])
                    :player (fsm/fsm-inc
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
                              [:player-dead]])
                    }
         :ctx/txs-fn-map (edn-resource "config/txs-fn-map.edn")
         :ctx/k->tick (edn-resource "config/k-tick.edn")
         :ctx/k->render (edn-resource "config/k->render.edn")
         :ctx/reaction-txs-fn-map (edn-resource "config/reaction-txs-fn-map.edn")
         :ctx/k->create (edn-resource "config/k->create.edn")
         :ctx/k->after-create (edn-resource "config/k->after-create.edn")
         :ctx/world-unit-scale (float (/ 48))
         :ctx/unit-scale (atom 1)

         ;frame
         :ctx/active-entities nil
         :ctx/delta-time nil
         :ctx/ui-mouse-position nil
         :ctx/world-mouse-position nil
         :ctx/mouseover-eid nil
         :ctx/paused? false

         ; stuff
         :ctx/elapsed-time 0
         :ctx/potential-field-cache (atom nil)
         :ctx/id-counter (atom 0)
         :ctx/entity-ids (atom {})

         ; constants (config & not state?)
         :ctx/schema (m/schema (edn-resource "config/app-schema.edn"))
         :ctx/factions-iterations {:good 15 :evil 5}
         :ctx/max-delta 0.04
         :ctx/minimum-size 0.39
         :ctx/z-orders [:z-order/on-ground
                        :z-order/ground
                        :z-order/flying
                        :z-order/effect]
         ))
