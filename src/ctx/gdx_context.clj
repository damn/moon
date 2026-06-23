(ns ctx.gdx-context
  (:require [gdx.audio :as audio]
            [gdx.files :as files]
            [gdx.graphics :as graphics]
            [gdx.input :as input]))

(defn f [ctx]
  (assoc ctx
         :ctx/audio    (audio/f)
         :ctx/files    (files/f)
         :ctx/graphics (graphics/f)
         :ctx/input    (input/f)))
