(ns render.if-not-paused.tick-entities
  (:require [game.ctx.do :refer [do!]]
            [game.ctx.tick-component :refer [tick-component]]
            [com.badlogic.gdx.scenes.scene2d.stage.add-actor :refer [add-actor!]]
            [moon.throwable :as throwable]
            [moon.ui.error-window :as error-window]))

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
     (add-actor! stage
                 (error-window/create
                  {:skin skin
                   :throwable t}))))
  ctx)
