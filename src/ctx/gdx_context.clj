(ns ctx.gdx-context
  (:require [clojure.gdx.gdx.audio :as audio]
            [clojure.gdx.gdx.files :as files]
            [clojure.gdx.gdx.input :as input]
            [clojure.gdx.gdx.graphics :as graphics]))

(defn f [ctx]
  (assoc ctx
         :ctx/audio    (audio/f)
         :ctx/files    (files/f)
         :ctx/graphics (graphics/f)
         :ctx/input    (input/f)))
