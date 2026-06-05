(ns game.ctx.mouse-position
  (:require [clojure.application :as app]
            [clojure.input :as input]))

(defn mouse-position [{:keys [ctx/app]}]
  [(input/x (app/input app))
   (input/y (app/input app))])
