(ns ctx.put-colors
  (:require [clojure.gdx.colors.put! :as colors-put!]
            [com.badlogic.gdx.graphics.color :as color]))

(defn step [_ctx]
  (colors-put!/f "PRETTY_NAME" (color/new [0.84 0.8 0.52 1])))
