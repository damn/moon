(ns moon.ui-actors.player-message
  (:require [clojure.gdx.utils.viewport :as viewport]
            [clojure.gdx.scene2d.actor :as actor]
            [clojure.gdx.scene2d.stage :as stage]
            [moon.draws :as draws]))

(defn create []
  (let [message-duration-seconds 0.5]
    (actor/create
     {:type :ui/actor
      :actor/name "player-message"
      :actor/user-object (atom nil)
      :draw! (fn [this _batch _parent-alpha]
               (when-let [stage (actor/stage this)]
                 (draws/handle (stage/ctx stage)
                               [(let [state (actor/user-object this)
                                      vp-width (viewport/world-width (stage/viewport stage))
                                      vp-height (viewport/world-height (stage/viewport stage))]
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
