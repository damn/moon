(ns moon.create.get-gdx-state
  (:require [clj.api.com.badlogic.gdx.gdx :as gdx]))

(defn step [ctx]
  (assoc ctx
         :ctx/audio    (gdx/audio)
         :ctx/graphics (gdx/graphics)
         :ctx/files    (gdx/files)
         :ctx/input    (gdx/input)))
