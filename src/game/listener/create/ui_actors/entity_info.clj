(ns game.listener.create.ui-actors.entity-info
  (:require [game.shared :refer [info-text]])
  (:import (com.badlogic.gdx.scenes.scene2d Actor Stage)
           (com.badlogic.gdx.scenes.scene2d.ui Label Skin Window)))

(defn- create-info-window
  [{:keys [title
           actor-name
           visible?
           position
           set-label-text!
           skin]}]
  (let [^Skin skin skin
        label (Label. "MY LABEL TEXT" skin)
        window (Window. ^String title skin)
        c (.add window label)]
    (.expand c)
    (.row window)
    (.pack window)
    (.setName window actor-name)
    (.setVisible window visible?)
    (let [[x y] position]
      (.setPosition window (float x) (float y)))
    (.addActor window (proxy [Actor] []
                         (act [delta]
                           (let [^Actor this this]
                             (when (.getStage this)
                               (.setText label ^String (set-label-text!)))
                             (.pack window)
                             (proxy-super act delta)))
                         (draw [batch parent-alpha])))
    window))

(defn stage-info-window-create
  [skin stage mouseover-eid elapsed-time]
  (create-info-window
   {:title "Entity Info"
    :actor-name "moon.ui.windows.entity-info"
    :visible? false
    :position [(.getWorldWidth (.getViewport ^Stage stage)) 0]
    :set-label-text! (fn []
                       (if-let [eid @mouseover-eid]
                         (info-text (apply dissoc @eid [:entity/skills
                                                        :entity/faction
                                                        :active-skill])
                                    @elapsed-time)
                         ""))
    :skin skin}))
