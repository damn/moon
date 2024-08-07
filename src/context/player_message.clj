(ns context.player-message
  (:require [core.component :refer [defcomponent] :as component]
            [api.context :as ctx :refer [->actor delta-time]]
            [api.graphics :as g]
            [api.tx :refer [transact!]]))

(defcomponent :context/player-message {}
  (component/create [_ _ctx]
    (atom nil)))

(defmethod transact! :tx/msg-to-player [[_ message] {:keys [context/player-message]}]
  (reset! player-message {:message message :counter 0})
  nil)

(def ^:private duration-seconds 1.5)

(defn- draw-player-message [g {:keys [context/player-message] :as ctx}]
  (when-let [{:keys [message]} @player-message]
    (g/draw-text g {:x (/ (ctx/gui-viewport-width ctx) 2)
                    :y (+ (/ (ctx/gui-viewport-height ctx) 2) 200)
                    :text message
                    :scale 2.5
                    :up? true})))

(defn- check-remove-message [{:keys [context/player-message] :as context}]
  (when-let [{:keys [counter]} @player-message]
    (swap! player-message update :counter + (delta-time context))
    (when (>= counter duration-seconds)
      (reset! player-message nil))))

(defn ->player-message-actor [ctx]
  (->actor ctx {:draw draw-player-message
                :act check-remove-message}))
