(ns clojure.ctx-data
  (:require [clojure.stage :as stage]
            [clojure.data-viewer-window :as data-viewer-window]))

(def item
  {:label "Ctx Data"
   :items [{:label "Show data"
            :on-click (fn [{:keys [ctx/skin
                                   ctx/stage] :as ctx}]
                        (stage/add-actor! stage
                                     (data-viewer-window/create
                                      {:title "Data View"
                                       :data ctx
                                       :width 1000
                                       :height 1000
                                       :skin skin}))
                        ctx)}]})
