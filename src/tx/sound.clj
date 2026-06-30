(ns tx.sound
  (:require [clojure.gdx :as gdx]))

(defn f
  [{:keys [ctx/audio] :as ctx} sound-name]
  (let [sounds audio]
    (assert (contains? sounds sound-name) (str sound-name))
    (gdx/sound-play (get sounds sound-name)))
  nil)
