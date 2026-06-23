(ns ctx.key-pressed
  (:require [gdl.key-pressed :as key-pressed?]))

(defn key-pressed? [{:keys [ctx/input]} input-key]
  (key-pressed?/f input input-key))
