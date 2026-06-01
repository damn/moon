(ns stage.player-message-actor
  (:require [game.ctx.draw :refer [draw!]]
            [clojure.gdx.scene2d.actor.create :as actor]
            [clojure.gdx.scene2d.actor.stage :refer [actor-stage]]
            [clojure.gdx.scene2d.actor.user-object :refer [actor-user-object]]))

(defn create [_ctx]
  (let [message-duration-seconds 0.5]
    (actor/create
     {:actor/name "player-message"
      :actor/user-object (atom nil)
      :draw! (fn [this _batch _parent-alpha]
               (when-let [stage (actor-stage this)]
                 (draw! (:stage/ctx stage)
                        [(let [state (actor-user-object this)
                               vp-width (:viewport/world-width (:stage/viewport stage))
                               vp-height (:viewport/world-height (:stage/viewport stage))]
                           (when-let [text (:text @state)]
                             [:draw/text {:x (/ vp-width 2)
                                          :y (+ (/ vp-height 2) 200)
                                          :text text
                                          :scale 2.5
                                          :up? true}]))])))
      :act! (fn [this delta]
              (let [state (actor-user-object this)]
                (when (:text @state)
                  (swap! state update :counter + delta)
                  (when (>= (:counter @state) message-duration-seconds)
                    (reset! state nil)))))})))
