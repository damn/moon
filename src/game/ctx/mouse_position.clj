(ns game.ctx.mouse-position
  (:require [gdx.application :as app]
            [gdx.input :as input]))

(defn mouse-position [{:keys [ctx/app]}]
  (input/position (app/input app)))
