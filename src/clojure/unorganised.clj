(ns clojure.unorganised
  (:require [clojure.edn-resource :refer [edn-resource]]
            [clojure.create-schema :refer [create-schema]]))

(defn step [ctx]
  (assoc ctx
         :ctx/k->destroy (edn-resource "config/k->destroy.edn")
         :ctx/k->after-create (edn-resource "config/k->after-create.edn")
         :ctx/k->state-enter (edn-resource "config/k->state-enter.edn")
         :ctx/k->state-exit (edn-resource "config/k->state-exit.edn")
         :ctx/k->handle-input (edn-resource "config/k->handle-input.edn")
         :ctx/k->cursor (edn-resource "config/k->cursor.edn")
         :ctx/k->clicked-inventory-cell (edn-resource "config/k->clicked-inventory-cell.edn")
         :ctx/draw-fns (edn-resource "config/draw-fns.edn")
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
         :ctx/show-potential-field-colors? nil
         :ctx/show-cell-entities? false
         :ctx/show-cell-occupied? false
         :ctx/show-body-bounds? false))
