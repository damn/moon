(ns moon.application.create.app
  (:import (com.badlogic.gdx Gdx)))

(defn step [_ctx]
  {:ctx/app       Gdx/app
   :ctx/graphics  Gdx/graphics
   :ctx/input     Gdx/input})
