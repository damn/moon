(ns clojure.ctx.gdx-context
  (:require [clojure.audio :as audio]
            [clojure.files :as files]
            [clojure.graphics :as graphics]
            [clojure.input :as input]))

(defn f [ctx]
  (assoc ctx
         :ctx/audio    (audio/f)
         :ctx/files    (files/f)
         :ctx/graphics (graphics/f)
         :ctx/input    (input/f)))
