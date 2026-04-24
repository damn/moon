(ns moon.create.gdx
  (:require [clojure.gdx :as gdx]))

(defn step [ctx]
  (assoc ctx
         :ctx/app       (gdx/app)
         :ctx/audio     (gdx/audio)
         :ctx/files     (gdx/files)
         :ctx/graphics  (gdx/graphics)
         :ctx/input     (gdx/input)))
