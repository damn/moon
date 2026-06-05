(ns create.unorganised
  (:require [clojure.core.edn-resource :refer [edn-resource]]
            [malli.core :as m]
            [reduce-fsm :as fsm]))

(defn- load-fsm [path]
  (let [data (edn-resource path)]
    (eval `(fsm/fsm-inc ~data))))

(defn step [ctx]
  (assoc ctx
         :ctx/fsms {:npc (load-fsm "config/npc-fsm.edn")
                    :player (load-fsm "config/player-fsm.edn")}
         :ctx/txs-fn-map (edn-resource "config/txs-fn-map.edn")
         :ctx/k->tick (edn-resource "config/k-tick.edn")
         :ctx/k->render (edn-resource "config/k->render.edn")
         :ctx/k->create (edn-resource "config/k->create.edn")
         :ctx/k->destroy (edn-resource "config/k->destroy.edn")
         :ctx/k->after-create (edn-resource "config/k->after-create.edn")
         :ctx/k->state-enter (edn-resource "config/k->state-enter.edn")
         :ctx/k->state-exit (edn-resource "config/k->state-exit.edn")
         :ctx/k->handle-input (edn-resource "config/k->handle-input.edn")
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
         :ctx/schema (m/schema (edn-resource "config/app-schema.edn"))
         :ctx/factions-iterations {:good 15 :evil 5}
         :ctx/max-delta 0.04
         :ctx/minimum-size 0.39
         :ctx/z-orders [:z-order/on-ground
                        :z-order/ground
                        :z-order/flying
                        :z-order/effect]))
