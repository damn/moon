(ns game.ctx.key-pressed
  (:require [clojure.application :as app]
            [clojure.input :as input]))

(defn key-pressed? [{:keys [ctx/app]} input-key]
  (input/key-pressed? (app/input app) input-key))
