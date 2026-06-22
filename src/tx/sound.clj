(ns tx.sound
  (:require [gdl.sound.play :as play]))

(defn f
  [{:keys [ctx/audio] :as ctx} sound-name]
  (let [sounds audio]
    (assert (contains? sounds sound-name) (str sound-name))
    (play/f! (get sounds sound-name)))
  nil)
