(ns clojure.player-message-actor
  (:require [clojure.actor :as gdx-actor]
            [clojure.draw :refer [draw!]]
            [clojure.scene2d-actor :as actor]))

(defn create [_ctx]
  (let [message-duration-seconds 0.5]
    (doto (actor/f
           {:draw! (fn [this _batch _parent-alpha]
                     (when-let [stage (gdx-actor/get-stage this)]
                       (draw! (:stage/ctx stage)
                              [(let [state (gdx-actor/get-user-object this)
                                     vp-width (:viewport/world-width (:stage/viewport stage))
                                     vp-height (:viewport/world-height (:stage/viewport stage))]
                                 (when-let [text (:text @state)]
                                   [:draw/text {:x (/ vp-width 2)
                                                :y (+ (/ vp-height 2) 200)
                                                :text text
                                                :scale 2.5
                                                :up? true}]))])))
            :act! (fn [this delta]
                    (let [state (gdx-actor/get-user-object this)]
                      (when (:text @state)
                        (swap! state update :counter + delta)
                        (when (>= (:counter @state) message-duration-seconds)
                          (reset! state nil)))))})
      (gdx-actor/set-name! "player-message")
      (gdx-actor/set-user-object! (atom nil)))))
