(ns render.if-not-paused.tick-entities
  (:require [ctx.do :refer [do!]]
            [ctx.tick-component :refer [tick-component]]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn f
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
     (Stage/.addActor stage
                      (error-window/create
                       {:skin skin
                        :throwable t}))))
  ctx)
