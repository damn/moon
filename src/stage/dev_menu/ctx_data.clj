(ns stage.dev-menu.ctx-data
  (:require [clojure.gdx.stage.add-actor :as add-actor]
            [gdx.scenes.scene2d.ui.data-viewer-window :as data-viewer-window]))

(def item
  {:label "Ctx Data"
   :items [{:label "Show data"
            :on-click (fn [{:keys [ctx/skin
                                   ctx/stage] :as ctx}]
                        (add-actor/f stage
                                     (data-viewer-window/create
                                      {:title "Data View"
                                       :data ctx
                                       :width 1000
                                       :height 1000
                                       :skin skin}))
                        ctx)}]})
