(ns clojure.moon.when-not-paused
  (:require [clojure.moon.ctx-do :refer [do!]]
            [clojure.ui.error-window :as error-window]
            [clojure.moon.factions-iterations :refer [factions-iterations]]
            [gdl.graphics :as graphics]
            [clojure.grid-update-potential-fields :as update-potential-fields]
            [clojure.max-delta :refer [max-delta]]
            [clojure.stage :as stage]
            [clojure.tick-component :refer [tick-component]]
            [clojure.throwable :as throwable]))

(defn- update-time
  [{:keys [ctx/graphics]
    :as ctx}]
  (let [delta-ms (min (graphics/get-delta-time graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn- update-potential-fields
  [{:keys [ctx/active-entities
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

(defn- tick-entities
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
    (do! ctx
         (mapcat (fn [eid]
                   (mapcat (fn [component]
                             (try (tick-component ctx eid component)
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

(defn f [ctx]
  (if (:ctx/paused? ctx)
    ctx
    (reduce (fn [ctx step] (step ctx))
            ctx
            [update-time
             update-potential-fields
             tick-entities])))
