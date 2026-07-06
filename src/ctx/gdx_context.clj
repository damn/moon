(ns ctx.gdx-context
  (:require [com.badlogic.gdx.gdx :as gdx]))

(defn f [ctx]
  (assoc ctx
         :ctx/audio    (gdx/audio)
         :ctx/files    (gdx/files)
         :ctx/graphics (gdx/graphics)
         :ctx/input    (gdx/input)))
