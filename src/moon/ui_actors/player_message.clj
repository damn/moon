(ns moon.ui-actors.player-message
  (:require [clojure.viewport :as viewport]
            [clojure.scene2d.actor :as actor]
            [moon.draws :as draws]
            [clojure.scene2d.stage :as stage]
            [moon.ui :as ui]))

(defn- draw-message [state vp-width vp-height]
  (when-let [text (:text @state)]
    [:draw/text {:x (/ vp-width 2)
                 :y (+ (/ vp-height 2) 200)
                 :text text
                 :scale 2.5
                 :up? true}]))

(def message-duration-seconds 0.5)

(defn create [_ctx]
  (ui/create
   {:type :ui/actor
    :actor/name "player-message"
    :actor/user-object (atom nil)
    :draw! (fn [this _batch _parent-alpha]
             (when-let [stage (actor/stage this)]
               (draws/handle! (stage/ctx stage)
                              [(draw-message (actor/user-object this)
                                             (viewport/world-width  (stage/viewport stage))
                                             (viewport/world-height (stage/viewport stage)))])))
    :act! (fn [this delta]
            (let [state (actor/user-object this)]
              (when (:text @state)
                (swap! state update :counter + delta)
                (when (>= (:counter @state) message-duration-seconds)
                  (reset! state nil)))))}))
