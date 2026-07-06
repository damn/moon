(ns ctx.button-just-pressed
  (:require [gdx.input.button-just-pressed :as button-just-pressed?]))

(defn button-just-pressed?
  [{:keys [ctx/input]} button-code]
  (button-just-pressed?/f input button-code))
