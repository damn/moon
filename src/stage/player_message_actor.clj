(ns stage.player-message-actor
  (:require [clojure.gdx.actor.get-stage :as get-stage]
            [clojure.gdx.actor.get-user-object :as get-user-object]
            [clojure.gdx.actor.set-name :as set-name]
            [clojure.gdx.actor.set-user-object :as set-user-object]
            [ctx.draw :refer [draw!]]
            [scene2d.actor :as actor]))

(defn create [_ctx]
  (let [message-duration-seconds 0.5]
    (doto (actor/f
           {:draw! (fn [this _batch _parent-alpha]
                     (when-let [stage (get-stage/f this)]
                       (draw! (:stage/ctx stage)
                              [(let [state (get-user-object/f this)
                                     vp-width (:viewport/world-width (:stage/viewport stage))
                                     vp-height (:viewport/world-height (:stage/viewport stage))]
                                 (when-let [text (:text @state)]
                                   [:draw/text {:x (/ vp-width 2)
                                                :y (+ (/ vp-height 2) 200)
                                                :text text
                                                :scale 2.5
                                                :up? true}]))])))
            :act! (fn [this delta]
                    (let [state (get-user-object/f this)]
                      (when (:text @state)
                        (swap! state update :counter + delta)
                        (when (>= (:counter @state) message-duration-seconds)
                          (reset! state nil)))))})
      (set-name/f "player-message")
      (set-user-object/f (atom nil)))))
