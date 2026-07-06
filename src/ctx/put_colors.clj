(ns ctx.put-colors
  (:require [com.badlogic.gdx.graphics.colors :as colors]
            [com.badlogic.gdx.graphics.color :as color]))

(defn step [_ctx]
  (colors/put! "PRETTY_NAME" (color/new [0.84 0.8 0.52 1])))
