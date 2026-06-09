(ns game.ctx.button-just-pressed
  (:require [com.badlogic.gdx.input.button-just-pressed :as button-just-pressed?]))

(defn button-just-pressed?
  [{:keys [ctx/input]} button-code]
  (button-just-pressed?/f input button-code))
