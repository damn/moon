(ns create.unorganised
  (:require [clojure.core-ext :refer [edn-resource]]
            [com.badlogic.gdx.scenes.scene2d.ui.tooltip-manager :as tooltip-manager]
            [com.badlogic.gdx.graphics.color :as color]
            [com.badlogic.gdx.graphics.colors :as colors]
            [malli.core :as m]))

(defn step [ctx]
  (tooltip-manager/set-initial-time! 0)
  (doseq [[name rgba] {"PRETTY_NAME" [0.84 0.8 0.52 1]}]
    (colors/put! name (color/create rgba)))
  (assoc ctx
         :ctx/txs-fn-map (edn-resource "config/txs-fn-map.edn")
         :ctx/k->tick (edn-resource "config/k-tick.edn")
         :ctx/k->render (edn-resource "config/k->render.edn")
         :ctx/reaction-txs-fn-map (edn-resource "config/reaction-txs-fn-map.edn")
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
