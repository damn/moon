(ns clojure.moon.player-message-actor-create
  (:require [com.badlogic.gdx.scenes.scene2d.actor :as actor]
            [clojure.moon.draw :refer [draw!]]
            [com.badlogic.gdx.utils.viewport.viewport :as viewport]))

(defn player-message-actor-create [_ctx]
  (let [message-duration-seconds 0.5]
    (doto (actor/new
           (fn [this delta]
             (let [state (actor/getUserObject this)]
               (when (:text @state)
                 (swap! state update :counter + delta)
                 (when (>= (:counter @state) message-duration-seconds)
                   (reset! state nil)))))
           (fn [this _batch _parent-alpha]
             (when-let [stage (actor/getStage this)]
               (draw! (:stage/ctx stage)
                      [(let [state (actor/getUserObject this)
                             vp-width (viewport/getWorldWidth (:stage/viewport stage))
                             vp-height (viewport/getWorldHeight (:stage/viewport stage))]
                         (when-let [text (:text @state)]
                           [:draw/text {:x (/ vp-width 2)
                                        :y (+ (/ vp-height 2) 200)
                                        :text text
                                        :scale 2.5
                                        :up? true}]))]))))
      (actor/setName "player-message")
      (actor/setUserObject (atom nil)))))
