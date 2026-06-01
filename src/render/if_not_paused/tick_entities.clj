(ns render.if-not-paused.tick-entities
  (:require [game.ctx.do :refer [do!]]
            [gdx.stage :as stage]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]))

(defn f
  [{:keys [ctx/k->tick
           ctx/active-entities
           ctx/skin
           ctx/stage]
    :as ctx}]
  (try
   (do! ctx
        (mapcat (fn [eid]
                  (mapcat (fn [[k v]]
                            (try (if-let [f (k->tick k)]
                                   (f v eid ctx)
                                   nil)
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
