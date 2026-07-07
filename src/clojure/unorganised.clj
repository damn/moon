(ns clojure.unorganised
  (:require [clojure.edn-resource :refer [edn-resource]]
            [clojure.create-schema :refer [create-schema]]
            [reduce-fsm :as fsm]))

(defn step [ctx]
  (assoc ctx
         :ctx/pausing? true
         :ctx/zoom-speed 0.025
         :ctx/info (edn-resource "config/info.edn")
         :ctx/fsms (let [load-fsm (fn [path]
                                    (let [data (edn-resource path)]
                                      (eval `(fsm/fsm-inc ~data))))]
                     {:npc (load-fsm "config/npc-fsm.edn")
                      :player (load-fsm "config/player-fsm.edn")})
         :ctx/k->tick (edn-resource "config/k-tick.edn")
         :ctx/k->render (edn-resource "config/k->render.edn")
         :ctx/k->create (edn-resource "config/k->create.edn")
         :ctx/k->destroy (edn-resource "config/k->destroy.edn")
         :ctx/k->after-create (edn-resource "config/k->after-create.edn")
         :ctx/k->state-enter (edn-resource "config/k->state-enter.edn")
         :ctx/k->state-exit (edn-resource "config/k->state-exit.edn")
         :ctx/k->handle-input (edn-resource "config/k->handle-input.edn")
         :ctx/k->cursor (edn-resource "config/k->cursor.edn")
         :ctx/k->clicked-inventory-cell (edn-resource "config/k->clicked-inventory-cell.edn")
         :ctx/state->pause-game? {
                                  :active-skill false
                                  :stunned false
                                  :player-moving false
                                  :player-idle true
                                  :player-dead true
                                  :player-item-on-cursor true
                                  }
         :ctx/draw-fns (edn-resource "config/draw-fns.edn")
         :ctx/world-unit-scale (float (/ 48))
         :ctx/unit-scale (atom 1)
         :ctx/active-entities nil
         :ctx/delta-time nil
         :ctx/ui-mouse-position nil
         :ctx/world-mouse-position nil
         :ctx/mouseover-eid nil
         :ctx/paused? false
         :ctx/elapsed-time 0
         :ctx/potential-field-cache (atom nil)
         :ctx/id-counter (atom 0)
         :ctx/entity-ids (atom {})
         :ctx/schema (create-schema (edn-resource "config/app-schema.edn"))
         :ctx/factions-iterations {:good 15 :evil 5}
         :ctx/max-delta 0.04
         :ctx/minimum-size 0.39
         :ctx/z-orders [:z-order/on-ground
                        :z-order/ground
                        :z-order/flying
                        :z-order/effect]
         :ctx/show-potential-field-colors? nil
         :ctx/show-cell-entities? false
         :ctx/show-cell-occupied? false
         :ctx/show-body-bounds? false))
