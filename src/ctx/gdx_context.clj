(ns ctx.gdx-context
  (:import (com.badlogic.gdx Gdx)))

(defn f [ctx]
  (assoc ctx
         :ctx/audio    (Gdx/audio)
         :ctx/files    (Gdx/files)
         :ctx/graphics (Gdx/graphics)
         :ctx/input    (Gdx/input)))
