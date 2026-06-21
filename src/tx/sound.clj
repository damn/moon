(ns tx.sound
  (:require [clojure.audio.sound.play :refer [play!]]))

(defn f
  [{:keys [ctx/audio] :as ctx} sound-name]
  (let [sounds audio]
    (assert (contains? sounds sound-name) (str sound-name))
    (play! (get sounds sound-name)))
  nil)
