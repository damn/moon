(ns moon.application.create.add-stage-actors.windows.info
  (:require [moon.ui.actor :as actor]
            [clojure.gdx.scene2d.stage :as stage]
            [clojure.gdx.utils.viewport :as viewport]
            [moon.info :as info]))

(defn create
  [{:keys [ctx/skin
           ctx/stage]}]
  (actor/create
   {:type :ui/info-window
    :title "Entity Info"
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
