(ns ctx.put-colors
  (:require [gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.graphics Colors)))

(defn step [_ctx]
  (Colors/put "PRETTY_NAME" (color/create [0.84 0.8 0.52 1])))
