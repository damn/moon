(ns moon.ui-actors.windows.info
  (:require [moon.ui :as ui]
            [moon.ui.info-window :as info-window]
            [moon.world.info :as info]))

(defn create
  [{:keys [ctx/skin
           ctx/stage]}]
  (info-window/create skin
                      {:title "Entity Info"
                       :actor-name "moon.ui.windows.entity-info"
                       :visible? false
                       :position [(ui/viewport-width stage) 0]
                       :set-label-text! (fn [{:keys [ctx/world]}]
                                          (if-let [eid (:world/mouseover-eid world)]
                                            (info/text (apply dissoc @eid [:entity/skills
                                                                           :entity/faction
                                                                           :active-skill])
                                                       world)
                                            ""))}))
