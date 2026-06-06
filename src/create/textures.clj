(ns create.textures
  (:require [gdx.application :as app]
            [gdx.textures]))

(defn step
  [ctx]
  (gdx.textures/create (app/files (:ctx/app ctx))))
