(ns moon.ui-actors.player-message
  (:require [moon.ctx :as ctx]
            [moon.stage :as stage])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)
           (com.badlogic.gdx.utils.viewport Viewport)
           (moon Stage)))

(defn- draw-message [state vp-width vp-height]
  (when-let [text (:text @state)]
    [:draw/text {:x (/ vp-width 2)
                 :y (+ (/ vp-height 2) 200)
                 :text text
                 :scale 2.5
                 :up? true}]))

(def message-duration-seconds 0.5)

(defn create [_ctx]
  (doto (proxy [Actor] []
          (draw [_batch _parent-alpha]
            (when-let [^Stage stage (Actor/.getStage this)]
              (ctx/draw! (.ctx stage)
                         [(draw-message (Actor/.getUserObject this)
                                        (Viewport/.getWorldWidth  (stage/viewport stage))
                                        (Viewport/.getWorldHeight (stage/viewport stage)))])))
          (act [delta]
            (let [state (Actor/.getUserObject this)]
              (when (:text @state)
                (swap! state update :counter + delta)
                (when (>= (:counter @state) message-duration-seconds)
                  (reset! state nil))))
            (let [^Actor this this]
              (proxy-super act delta))))
    (.setName "player-message")
    (.setUserObject (atom nil))))
