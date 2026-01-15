(ns moon.render.clear-screen
  (:require [moon.color :as color]
            [moon.graphics :as graphics]))

(defn do!
  [{:keys [ctx/graphics] :as ctx}]
  (graphics/clear-screen! graphics color/black)
  ctx)
