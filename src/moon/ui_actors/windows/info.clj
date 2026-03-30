(ns moon.ui-actors.windows.info
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport]
            [moon.actor :as actor]
            [moon.info :as info]
            [moon.table :as table])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin
                                               Window)
           (moon Stage)))

(defn- create*
  [^Skin skin
   {:keys [title
           actor-name
           visible?
           position
           set-label-text!]}]
  (let [label (Label. "" skin)
        window (doto (Window. ^String title skin)
                 (table/add-rows! [[{:actor label :expand? true}]])
                 (.setName actor-name)
                 (.setVisible visible?)
                 (actor/set-position! position))]
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
            :position [(viewport/world-width (Stage/.getViewport stage)) 0]
            :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                                   :as ctx}]
                               (if-let [eid mouseover-eid]
                                 (info/text (apply dissoc @eid [:entity/skills
                                                                :entity/faction
                                                                :active-skill])
                                            ctx)
                                 ""))}))
