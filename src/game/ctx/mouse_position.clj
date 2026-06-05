(ns game.ctx.mouse-position
  (:require [clojure.application :as app]
            [clojure.input :as input]))

(defn mouse-position [{:keys [ctx/app]}]
  (input/position (app/input app)))
