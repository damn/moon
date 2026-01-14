(ns moon.ui.message)

(defprotocol PlayerMessage
  (show! [_ text]))
