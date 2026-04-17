(ns moon.if-not-paused.tick-entities
  (:require [moon.entity :as entity]
            [clojure.scene2d.stage :as stage]
            [moon.throwable :as throwable]
            [moon.txs :as txs]))

(defn do!
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
                       ((get (:ctx/actor-fns ctx) :ui/error-window)
                        {:skin skin
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
