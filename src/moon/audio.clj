(ns moon.audio)

(defprotocol Audio
  (new-sound [_ path]))
