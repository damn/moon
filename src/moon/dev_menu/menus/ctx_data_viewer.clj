(ns moon.dev-menu.menus.ctx-data-viewer
  (:require [clojure.scene2d.actor :as actor]
            [clojure.scene2d.stage :as stage]))

(defn create [_ctx]
  {:label "Ctx Data"
   :items [{:label "Show data"
            :on-click (fn [_actor {:keys [ctx/skin
                                          ctx/stage] :as ctx}]
                        (stage/add-actor! stage
                                          (actor/create
                                           {:type :ui/data-viewer-window
                                            :title "Data View"
                                            :data ctx
                                            :width 1000
                                            :height 1000
                                            :skin skin})))}]})
