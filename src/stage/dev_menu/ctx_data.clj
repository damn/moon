(ns stage.dev-menu.ctx-data
  (:require [gdx.stage :as stage]
            [gdx.scenes.scene2d.ui.data-viewer-window :as data-viewer-window]))

(def item
  {:label "Ctx Data"
   :items [{:label "Show data"
            :on-click (fn [_actor {:keys [ctx/skin
                                          ctx/stage] :as ctx}]
                        (stage/add-actor! stage
                                          (data-viewer-window/create
                                           {:title "Data View"
                                            :data ctx
                                            :width 1000
                                            :height 1000
                                            :skin skin})))}]})
