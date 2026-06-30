(ns ctx.put-colors
  (:require [clojure.gdx :as gdx]))

(defn step [_ctx]
  (gdx/colors-put! "PRETTY_NAME" (gdx/color [0.84 0.8 0.52 1])))
