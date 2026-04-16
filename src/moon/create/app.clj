(ns moon.create.app
  (:require [clj.api.com.badlogic.gdx.gdx :as gdx]
            [clj.api.com.badlogic.gdx.graphics.g2d.sprite-batch :as sprite-batch]))

(defn step [ctx]
  (let [batch (sprite-batch/create)]
    (assoc ctx
           :ctx/app      (gdx/app)
           :ctx/audio    (gdx/audio)
           :ctx/graphics (gdx/graphics)
           :ctx/files    (gdx/files)
           :ctx/input    (gdx/input)
           :ctx/batch batch
           )))
