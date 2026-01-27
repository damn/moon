(ns moon.ui-actors.windows.info
  (:require [moon.info :as info]
            [moon.ui.actor :as actor]
            [moon.ui.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin
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
  (let [label (Label. "" ^Skin skin)
        ^Window window (doto (window/create
                              {:skin skin
                               :title title
                               :rows [[{:actor label
                                        :expand? true}]]})
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
            :position [(Viewport/.getWorldWidth (Stage/.getViewport stage)) 0]
            :set-label-text! (fn [{:keys [ctx/mouseover-eid]
                                   :as ctx}]
                               (if-let [eid mouseover-eid]
                                 (info/text (apply dissoc @eid [:entity/skills
                                                                :entity/faction
                                                                :active-skill])
                                            ctx)
                                 ""))}))
