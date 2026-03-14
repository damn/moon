(ns moon.dev-menu.menus.ctx-data-viewer
  (:require [moon.data-viewer-window :as data-viewer-window])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn create [_ctx]
  {:label "Ctx Data"
   :items [{:label "Show data"
            :on-click (fn [_actor {:keys [ctx/skin
                                          ctx/stage] :as ctx}]
                        (Stage/.addActor stage
                                         (data-viewer-window/create
                                          {:title "Data View"
                                           :data ctx
                                           :width 1000
                                           :height 1000
                                           :skin skin})))}]})
