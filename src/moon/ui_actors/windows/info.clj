(ns moon.ui-actors.windows.info
  (:require [moon.info :as info]
            [moon.stage :as stage]
            [moon.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn- create*
  [skin
   {:keys [title
           actor-name
           visible?
           position
           set-label-text!]}]
  (let [label (ui/actor
               {:type :ui/label
                :label/text ""
                :label/skin skin})
        window (ui/actor
                {:type :ui/window
                 :skin skin
                 :title title
                 :actor/name actor-name
                 :actor/visible? visible?
                 :actor/position position
                 :rows [[{:actor label
                          :expand? true}]]})]
    (.addActor window (proxy [Actor] []
                        (act [delta]
                          (when-let [stage (.getStage this)]
                            (.setText label (set-label-text! (.ctx stage))))
                          (.pack window)
                          (let [^Actor this this]
                            (proxy-super act delta)))))
    window))

(defn create
  [{:keys [ctx/skin
           ctx/stage]}]
  (create* skin
           {:title "Entity Info"
            :actor-name "moon.ui.windows.entity-info"
            :visible? false
            :position [(Viewport/.getWorldWidth (stage/viewport stage)) 0]
            :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                                   :as ctx}]
                               (if-let [eid mouseover-eid]
                                 (info/text (apply dissoc @eid [:entity/skills
                                                                :entity/faction
                                                                :active-skill])
                                            ctx)
                                 ""))}))
