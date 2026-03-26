(ns moon.dev-menu.menus.ctx-data-viewer
  (:require [moon.data-viewer-window :as data-viewer-window]
            [moon.stage :as stage]))

(defn create [_ctx]
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
