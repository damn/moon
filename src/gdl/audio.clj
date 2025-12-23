(ns gdl.audio)

(defprotocol Audio
  (new-sound [_ file-handle]))
