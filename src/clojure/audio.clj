(ns clojure.audio)

(defprotocol Audio
  (new-sound [_ file-handle]))
