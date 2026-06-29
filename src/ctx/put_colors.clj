(ns ctx.put-colors
  (:require [clojure.gdx.new-color :as new-color])
  (:import (com.badlogic.gdx.graphics Colors)))

(defn step [_ctx]
  (Colors/put "PRETTY_NAME" (new-color/f [0.84 0.8 0.52 1])))
