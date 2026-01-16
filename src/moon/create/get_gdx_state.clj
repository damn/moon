(ns moon.create.get-gdx-state
  (:import (com.badlogic.gdx Gdx)))

(defn step [ctx]
  (assoc ctx
         :ctx/audio    Gdx/audio
         :ctx/graphics Gdx/graphics
         :ctx/files    Gdx/files
         :ctx/input    Gdx/input))
