(ns game.ctx.mouse-position
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.input :as input]))

(defn mouse-position [{:keys [ctx/app]}]
  (input/position (app/input app)))
