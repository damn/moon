(ns game.ctx.clear-screen
  (:require [com.badlogic.gdx.graphics.clear :as clear]))

(defn clear-screen!
  [{:keys [ctx/graphics]} r g b a]
  (clear/f! graphics r g b a))
