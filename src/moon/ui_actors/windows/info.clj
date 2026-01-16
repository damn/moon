(ns moon.ui-actors.windows.info
  (:require [moon.info :as info]
            [moon.ui.info-window :as info-window])
  (:import (com.badlogic.gdx.utils.viewport Viewport)))

(defn create
  [{:keys [ctx/skin
           ctx/stage]}]
  (info-window/create skin
                      {:title "Entity Info"
                       :actor-name "moon.ui.windows.entity-info"
                       :visible? false
                       :position [(Viewport/.getWorldWidth (.getViewport stage)) 0]
                       :set-label-text! (fn [{:keys [ctx/mouseover-eid
                                                     ctx/world]}]
                                          (if-let [eid mouseover-eid]
                                            (info/text (apply dissoc @eid [:entity/skills
                                                                           :entity/faction
                                                                           :active-skill])
                                                       world)
                                            ""))}))
