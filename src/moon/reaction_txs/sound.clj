(ns moon.reaction-txs.sound
  (:require [clj.api.com.badlogic.gdx.audio.sound :as sound]))

(defn do!
  [{:keys [ctx/audio] :as ctx} sound-name]
  (let [sounds audio]
    (assert (contains? sounds sound-name) (str sound-name))
    (sound/play! (get sounds sound-name)))
  ctx)
