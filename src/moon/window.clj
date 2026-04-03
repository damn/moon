(ns moon.window)

(defprotocol Window
  (add-close-button! [_ skin]))
