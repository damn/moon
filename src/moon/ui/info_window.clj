(ns moon.ui.info-window
  (:require [moon.ui.window :as window])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.scenes.scene2d.ui Label
                                               Skin)))

(defn create
  [skin
   {:keys [title
           actor-name
           visible?
           position
           set-label-text!]}]
  (let [label (Label. "" ^Skin skin)
        window (window/create {:skin skin
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
