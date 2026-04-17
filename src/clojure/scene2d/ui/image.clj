(ns clojure.scene2d.ui.image)

(defprotocol Image
  (set-drawable! [_ drawable]))
