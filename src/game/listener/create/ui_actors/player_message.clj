(ns game.listener.create.ui-actors.player-message
  (:require [game.draw :refer [draw-fn-text]])
  (:import (com.badlogic.gdx.scenes.scene2d Stage)))

(defn player-message-actor-create [default-font unit-scale]
  (let [message-duration-seconds 0.5]
    (doto (proxy [com.badlogic.gdx.scenes.scene2d.Actor] []
            (act [delta]
              (let [state (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor this)]
                (when (:text @state)
                  (swap! state update :counter + delta)
                  (when (>= (:counter @state) message-duration-seconds)
                    (reset! state nil))))
              (let [^com.badlogic.gdx.scenes.scene2d.Actor this this]
                (proxy-super act delta)))
            (draw [batch parent-alpha]
              (when-let [stage (.getStage ^com.badlogic.gdx.scenes.scene2d.Actor this)]
                (let [state (.getUserObject ^com.badlogic.gdx.scenes.scene2d.Actor this)
                      vp-width (.getWorldWidth (.getViewport ^Stage stage))
                      vp-height (.getWorldHeight (.getViewport ^Stage stage))]
                  (when-let [text (:text @state)]
                    (draw-fn-text batch default-font unit-scale {:x (/ vp-width 2)
                                       :y (+ (/ vp-height 2) 200)
                                       :text text
                                       :scale 2.5
                                       :up? true}))))))
      (.setName "player-message")
      (.setUserObject (atom nil)))))
