(ns clojure.ctx.gdx-context
  (:require [gdl.audio :as audio]
            [gdl.files :as files]
            [gdl.graphics :as graphics]
            [gdl.input :as input]))

(defn f [ctx]
  (assoc ctx
         :ctx/audio    (audio/f)
         :ctx/files    (files/f)
         :ctx/graphics (graphics/f)
         :ctx/input    (input/f)))
