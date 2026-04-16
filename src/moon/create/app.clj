(ns moon.create.app
  (:require [clj.api.com.badlogic.gdx.gdx :as gdx]))

(defn step [ctx]
  (assoc ctx
         :ctx/app      (gdx/app)
         :ctx/audio    (gdx/audio)
         :ctx/graphics (gdx/graphics)
         :ctx/files    (gdx/files)
         :ctx/input    (gdx/input)))
