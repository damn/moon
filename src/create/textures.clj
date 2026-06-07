(ns create.textures
  (:require [com.badlogic.gdx.application :as app]
            [gdx.textures]))

(defn step
  [ctx]
  (gdx.textures/create (app/files (:ctx/app ctx))))
