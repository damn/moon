(ns moon.ui.message
  (:require [moon.graphics :as graphics])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn- draw-message [state vp-width vp-height]
  (when-let [text (:text @state)]
    [:draw/text {:x (/ vp-width 2)
                 :y (+ (/ vp-height 2) 200)
                 :text text
                 :scale 2.5
                 :up? true}]))

(defn create [duration-seconds]
  (doto (proxy [Actor] []
          (draw [_batch _parent-alpha]
            (when-let [stage (.getStage this)]
              (graphics/draw! (:ctx/graphics (.ctx stage))
                              [(draw-message (Actor/.getUserObject this)
                                             (Viewport/.getWorldWidth  (.getViewport stage))
                                             (Viewport/.getWorldHeight (.getViewport stage)))])))
          (act [delta]
            (let [state (Actor/.getUserObject this)]
              (when (:text @state)
                (swap! state update :counter + delta)
                (when (>= (:counter @state) duration-seconds)
                  (reset! state nil))))
            (let [^Actor this this]
              (proxy-super act delta))))
    (.setName "player-message")
    (.setUserObject (atom nil))))

(defn show! [this text]
  (.setUserObject this (atom {:text text
                              :counter 0})))
