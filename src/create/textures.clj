(ns create.textures
  (:require [gdx.textures]))

(defn step
  [ctx]
  (gdx.textures/create (:ctx/files ctx)))
