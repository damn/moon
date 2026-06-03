(ns stage.player-message-actor
  (:require [game.ctx.draw :refer [draw!]]
            [clojure.gdx.scene2d.actor.get-user-object :refer [get-user-object]]
            [clojure.gdx.scene2d.actor.get-stage :refer [get-stage]]
            [clojure.gdx.scene2d.actor.set-name :refer [set-name!]]
            [clojure.gdx.scene2d.actor.set-user-object :refer [set-user-object!]]
            [clojure.gdx.scene2d.actor.create :as actor]))

(defn create [_ctx]
  (let [message-duration-seconds 0.5]
    (doto (actor/create
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
