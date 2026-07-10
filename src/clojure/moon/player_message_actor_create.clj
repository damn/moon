(ns clojure.moon.player-message-actor-create
  (:require [gdl.actor :as actor]
            [clojure.moon.draw :refer [draw!]]
            [clojure.scene2d-actor :as scene2d-actor]
            [gdl.viewport :as viewport]))

(defn player-message-actor-create [_ctx]
  (let [message-duration-seconds 0.5]
    (doto (scene2d-actor/f
           {:draw! (fn [this _batch _parent-alpha]
                     (when-let [stage (actor/get-stage this)]
                       (draw! (:stage/ctx stage)
                              [(let [state (actor/get-user-object this)
                                     vp-width (viewport/get-world-width (:stage/viewport stage))
                                     vp-height (viewport/get-world-height (:stage/viewport stage))]
                                 (when-let [text (:text @state)]
                                   [:draw/text {:x (/ vp-width 2)
                                                :y (+ (/ vp-height 2) 200)
                                                :text text
                                                :scale 2.5
                                                :up? true}]))])))
            :act! (fn [this delta]
                    (let [state (actor/get-user-object this)]
                      (when (:text @state)
                        (swap! state update :counter + delta)
                        (when (>= (:counter @state) message-duration-seconds)
                          (reset! state nil)))))})
      (actor/set-name "player-message")
      (actor/set-user-object (atom nil)))))
