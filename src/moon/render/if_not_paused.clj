(ns moon.render.if-not-paused
  (:require [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.graphics :as graphics]
            [moon.grid :as grid]
            [moon.txs :as txs]
            [moon.entity :as entity]
            [moon.throwable :as throwable]))

(defn update-time
  [{:keys [ctx/graphics
           ctx/max-delta]
    :as ctx}]
  (let [delta-ms (min (graphics/delta-time graphics) max-delta)]
    (-> ctx
        (assoc :ctx/delta-time delta-ms)
        (update :ctx/elapsed-time + delta-ms))))

(defn tick-entities!
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
   (txs/handle! ctx (mapcat (fn [eid]
                              (mapcat (fn [[k v]]
                                        (try (entity/tick [k v] eid ctx)
                                             (catch Throwable t
                                               (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                                      @eid))
                            active-entities))
   (catch Throwable t
     (throwable/pretty-pst t)
     (stage/add-actor! stage
                       (actor/create
                        {:type :ui/error-window
                         :skin skin
                         :throwable t}))))
  ctx)

(comment
 (= (tick-entities! {:ctx/active-entities [(atom {:firstk :foo
                                                    :secondk :bar})
                                             (atom {:firstk2 :foo2
                                                    :secondk2 :bar2})]}
                    {:firstk (fn [v eid world]
                               [[:foo/bar]])
                     :secondk (fn [v eid world]
                                [[:foo/barz]
                                 [:asdf]])
                     :firstk2 (fn [v eid world]
                                nil)
                     :secondk2 (fn [v eid world]
                                 [[:asdf2] [:asdf3]])})
    [[:foo/bar]
     [:foo/barz]
     [:asdf]
     [:asdf2]
     [:asdf3]])
 )

(defn update-potential-fields
  [{:keys [ctx/active-entities
           ctx/factions-iterations
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations]
    (grid/tick! potential-field-cache
                grid
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
        update-potential-fields
        tick-entities!)))
