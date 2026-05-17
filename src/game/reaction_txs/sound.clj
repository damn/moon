(ns game.reaction-txs.sound)

(defn do!
  [{:keys [ctx/audio] :as ctx} sound-name]
  (let [sounds audio]
    (assert (contains? sounds sound-name) (str sound-name))
    (com.badlogic.gdx.audio.Sound/.play (get sounds sound-name)))
  ctx)
