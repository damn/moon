(ns moon.ui-actors.player-message
  (:require [moon.ui.message :as message])
  ) ; TODO moon.stage.*?

(def message-duration-seconds 0.5)

(defn create [_ctx]
  (message/create message-duration-seconds))
