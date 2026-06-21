(ns game.ctx.key-pressed
  (:require [clojure.input.key-pressed :as key-pressed?]))

(defn key-pressed? [{:keys [ctx/input]} input-key]
  (key-pressed?/f input input-key))
