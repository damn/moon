(ns moon.render.tick-entities
  (:require [moon.ctx :as ctx]
            [moon.entity :as entity]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]))

(defn do!
  [{:keys [ctx/skin
           ctx/stage
           ctx/paused?
           ctx/world]
    :as ctx}]
  (if paused?
    ctx
    (do (try
         (ctx/handle! ctx (mapcat (fn [eid]
                                    (mapcat (fn [[k v]]
                                              (try (entity/tick [k v] eid world)
                                                   (catch Throwable t
                                                     (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                                            @eid))
                                  (:world/active-entities world)))
         (catch Throwable t
           (throwable/pretty-pst t)
           (.addActor stage (error-window/create skin t))))
        ctx)))

(comment
 (= (tick-entities! {:world/active-entities [(atom {:firstk :foo
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
