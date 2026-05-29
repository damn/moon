(ns render.if-not-paused
  (:require [gdx.application :as app]
            [gdx.graphics :as graphics]
            [gdx.scenes.scene2d.stage :as stage]
            [game.ctx :as ctx]
            [moon.ui.error-window :as error-window]
            [moon.grid :as grid]
            [game.entity :as entity]
            [moon.throwable :as throwable]
            )
  )

(defn delta-time
  [{:keys [ctx/app]}]
  (graphics/delta-time (app/graphics app)))

(defn tick-entities!
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
   (ctx/do! ctx (mapcat (fn [eid]
                          (mapcat (fn [[k v]]
                                    (try (entity/tick [k v] eid ctx)
                                         (catch Throwable t
                                           (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                                  @eid))
                        active-entities))
   (catch Throwable t
     (throwable/pretty-pst t)
     (stage/add-actor! stage
                       (error-window/create
                        {:skin skin
                         :throwable t}))))
  ctx)

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
    (grid/tick! grid
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
        tick-entities!)))
