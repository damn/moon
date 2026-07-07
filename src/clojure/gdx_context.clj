(ns clojure.gdx-context
  (:require [clojure.gdx.audio :as audio]
            [clojure.gdx.files :as files]
            [clojure.gdx.graphics :as graphics]
            [clojure.gdx.input :as input]))

(defn f [ctx]
  (assoc ctx
         :ctx/audio    (audio/f)
         :ctx/files    (files/f)
         :ctx/graphics (graphics/f)
         :ctx/input    (input/f)))
