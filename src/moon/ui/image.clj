(ns moon.ui.image)

(defprotocol Image
  (set-drawable! [_ drawable]))
