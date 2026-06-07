(ns game.ctx.key-just-pressed
  (:require [com.badlogic.gdx.input :as input]))

(defn key-just-pressed? [{:keys [ctx/input]} input-key]
  (input/key-just-pressed? input input-key))
