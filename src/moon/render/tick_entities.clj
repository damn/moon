(ns moon.render.tick-entities
  (:require [moon.ctx :as ctx]
            [moon.entity :as entity]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn do!
  [{:keys [ctx/active-entities
           ctx/skin
           ctx/stage
           ctx/paused?]
    :as ctx}]
  (if paused?
    ctx
    (do (try
         (ctx/handle! ctx (mapcat (fn [eid]
                                    (mapcat (fn [[k v]]
                                              (try (entity/tick [k v] eid ctx)
                                                   (catch Throwable t
                                                     (throw (ex-info "Error at `entity/tick`:" {:eid eid} t)))))
                                            @eid))
                                  active-entities))
         (catch Throwable t
           (throwable/pretty-pst t)
           (Stage/.addActor stage
                            (error-window/create
                             {:skin skin
                              :throwable t}))))
        ctx)))

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
