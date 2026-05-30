(ns create.textures
  (:require [com.badlogic.gdx.application :as app]
            [gdx.textures]))

(defn step
  [ctx]
  (assoc ctx :ctx/textures (gdx.textures/create (app/files (:ctx/app ctx)))))
