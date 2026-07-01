(ns ctx.put-colors
  (:require [clojure.gdx.colors.put! :as colors-put!]
            [clojure.gdx.new-color :as new-color]))

(defn step [_ctx]
  (colors-put!/f "PRETTY_NAME" (new-color/f [0.84 0.8 0.52 1])))
