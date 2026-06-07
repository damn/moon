(ns game.ctx.button-just-pressed
  (:require [com.badlogic.gdx.input :as input]))

(defn button-just-pressed?
  [{:keys [ctx/input]} button-code]
  (input/button-just-pressed? input button-code))
