(ns moon.ui-actors.windows.info
  (:require [moon.info :as info]
            [moon.ui :as ui])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Window)
           (com.badlogic.gdx.utils.viewport Viewport)
           (moon Stage)))

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
        ^Window window (ui/actor
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
                          (when-let [^Stage stage (Actor/.getStage this)]
                            (Label/.setText label ^String (set-label-text! (.ctx stage))))
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
            :position [(Viewport/.getWorldWidth (Stage/.getViewport stage)) 0]
            :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                                   :as ctx}]
                               (if-let [eid mouseover-eid]
                                 (info/text (apply dissoc @eid [:entity/skills
                                                                :entity/faction
                                                                :active-skill])
                                            ctx)
                                 ""))}))
