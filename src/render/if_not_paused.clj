(ns render.if-not-paused
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.graphics :as graphics]
            [moon.grid.update-potential-fields :as update-potential-fields]
            render.if-not-paused.tick-entities))

(defn delta-time [{:keys [ctx/app]}]
  (graphics/delta-time (app/graphics app)))

(defn update-time
  [{:keys [ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (delta-time ctx) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn update-potential-fields!
  [{:keys [ctx/active-entities
           ctx/factions-iterations
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations]
    (update-potential-fields/tick! grid
                                   potential-field-cache
                                   faction
                                   active-entities
                                   max-iterations))
  ctx)

(defn step
  [{:keys [ctx/paused?]
    :as ctx}]
  (if paused?
    ctx
    (-> ctx
        update-time
        update-potential-fields!
        render.if-not-paused.tick-entities/f)))
