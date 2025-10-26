(ns moon.game.render.clear-screen
  (:require [moon.graphics :as graphics]
            [moon.color :as color]))

(defn step
  [{:keys [ctx/graphics] :as ctx}]
  (graphics/clear-screen! graphics color/black)
  ctx)
