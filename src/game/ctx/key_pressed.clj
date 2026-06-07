(ns game.ctx.key-pressed
  (:require [com.badlogic.gdx.input :as input]))

(defn key-pressed? [{:keys [ctx/input]} input-key]
  (input/key-pressed? input input-key))
