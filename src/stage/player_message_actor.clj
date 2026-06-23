(ns stage.player-message-actor
  (:require [ctx.draw :refer [draw!]]
            [scene2d.actor.get-user-object :refer [get-user-object]]
            [gdl.get-stage :refer [get-stage]]
            [gdl.set-name :refer [set-name!]]
            [gdl.set-user-object :refer [set-user-object!]]
            [scene2d.actor :as actor]))

(defn create [_ctx]
  (let [message-duration-seconds 0.5]
    (doto (actor/f
           {:draw! (fn [this _batch _parent-alpha]
                     (when-let [stage (get-stage this)]
                       (draw! (:stage/ctx stage)
                              [(let [state (get-user-object this)
                                     vp-width (:viewport/world-width (:stage/viewport stage))
                                     vp-height (:viewport/world-height (:stage/viewport stage))]
                                 (when-let [text (:text @state)]
                                   [:draw/text {:x (/ vp-width 2)
                                                :y (+ (/ vp-height 2) 200)
                                                :text text
                                                :scale 2.5
                                                :up? true}]))])))
            :act! (fn [this delta]
                    (let [state (get-user-object this)]
                      (when (:text @state)
                        (swap! state update :counter + delta)
                        (when (>= (:counter @state) message-duration-seconds)
                          (reset! state nil)))))})
      (set-name! "player-message")
      (set-user-object! (atom nil)))))
