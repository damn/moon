(ns clojure.moon.player-message-actor-create
  (:require [clojure.scene2d.actor.get-stage]
            [clojure.scene2d.actor.get-user-object]
            [clojure.scene2d.actor.set-name :as set-name]
            [clojure.scene2d.actor.set-user-object :as set-user-object]
            [clojure.draw :refer [draw!]]
            [clojure.scene2d-actor :as actor]))

(defn player-message-actor-create [_ctx]
  (let [message-duration-seconds 0.5]
    (doto (actor/f
           {:draw! (fn [this _batch _parent-alpha]
                     (when-let [stage (clojure.scene2d.actor.get-stage/f this)]
                       (draw! (:stage/ctx stage)
                              [(let [state (clojure.scene2d.actor.get-user-object/f this)
                                     vp-width (:viewport/world-width (:stage/viewport stage))
                                     vp-height (:viewport/world-height (:stage/viewport stage))]
                                 (when-let [text (:text @state)]
                                   [:draw/text {:x (/ vp-width 2)
                                                :y (+ (/ vp-height 2) 200)
                                                :text text
                                                :scale 2.5
                                                :up? true}]))])))
            :act! (fn [this delta]
                    (let [state (clojure.scene2d.actor.get-user-object/f this)]
                      (when (:text @state)
                        (swap! state update :counter + delta)
                        (when (>= (:counter @state) message-duration-seconds)
                          (reset! state nil)))))})
      (set-name/f "player-message")
      (set-user-object/f (atom nil)))))
