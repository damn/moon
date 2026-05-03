(ns moon.application.create.app
  (:require [com.badlogic.gdx.gdx :as gdx]))

(defn step [_ctx]
  {:ctx/app       (gdx/app)
   :ctx/graphics  (gdx/graphics)
   :ctx/input     (gdx/input)})
