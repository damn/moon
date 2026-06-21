(ns game.ctx.key-just-pressed
  (:require [clojure.input.key-just-pressed :as key-just-pressed?]))

(defn key-just-pressed? [{:keys [ctx/input]} input-key]
  (key-just-pressed?/f input input-key))
