(ns reaction-txs.sound
  (:import (com.badlogic.gdx.audio Sound)))

(defn f
  [{:keys [ctx/audio] :as ctx} sound-name]
  (let [sounds audio]
    (assert (contains? sounds sound-name) (str sound-name))
    (Sound/.play (get sounds sound-name)))
  ctx)
