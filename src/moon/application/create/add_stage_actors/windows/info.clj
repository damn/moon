(ns moon.application.create.add-stage-actors.windows.info
  (:require [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.utils.viewport :as viewport]
            [moon.info :as info]
            [moon.ui-actors.windows.info]))

(defn create
  [{:keys [ctx/skin
           ctx/stage]}]
  (moon.ui-actors.windows.info/create
   {:title "Entity Info"
    :actor-name "moon.ui.windows.entity-info"
    :visible? false
    :position [(viewport/world-width (stage/viewport stage)) 0]
    :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                           :as ctx}]
                       (if-let [eid mouseover-eid]
                         (info/text (apply dissoc @eid [:entity/skills
                                                        :entity/faction
                                                        :active-skill])
                                    ctx)
                         ""))
    :skin skin}))
