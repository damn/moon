(ns moon.ui-actors.player-message
  (:require [clj.api.com.badlogic.gdx.scenes.scene2d.actor :as gdx-actor]
            [gdl.viewport :as viewport]
            [moon.actor :as actor]
            [moon.draws :as draws]
            [moon.stage :as stage]))

(defn- draw-message [state vp-width vp-height]
  (when-let [text (:text @state)]
    [:draw/text {:x (/ vp-width 2)
                 :y (+ (/ vp-height 2) 200)
                 :text text
                 :scale 2.5
                 :up? true}]))

(def message-duration-seconds 0.5)

(defn create [_ctx]
  (doto (gdx-actor/create
         {:draw! (fn [this _batch _parent-alpha]
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
                        (reset! state nil)))))})
    (actor/set-name! "player-message")
    (actor/set-user-object! (atom nil))))
