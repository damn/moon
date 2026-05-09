(ns moon.application.create.add-stage-actors.player-message
  (:require [moon.ui.actor :as actor]
            [moon.stage :as stage]
            [moon.draws :as draws]))

(defn create [_ctx]
  (let [message-duration-seconds 0.5]
    (actor/create
     {:type :ui/actor
      :actor/name "player-message"
      :actor/user-object (atom nil)
      :draw! (fn [this _batch _parent-alpha]
               (when-let [stage (actor/stage this)]
                 (draws/handle (stage/ctx stage)
                               [(let [state (actor/user-object this)
                                      vp-width (stage/viewport-width stage)
                                      vp-height (stage/viewport-height stage)]
                                  (when-let [text (:text @state)]
                                    [:draw/text {:x (/ vp-width 2)
                                                 :y (+ (/ vp-height 2) 200)
                                                 :text text
                                                 :scale 2.5
                                                 :up? true}]))])))
      :act! (fn [this delta]
              (let [state (actor/user-object this)]
                (when (:text @state)
                  (swap! state update :counter + delta)
                  (when (>= (:counter @state) message-duration-seconds)
                    (reset! state nil)))))})))
