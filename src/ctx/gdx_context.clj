(ns ctx.gdx-context
  (:require [clojure.gdx :as gdx]))

(defn f [ctx]
  (assoc ctx
         :ctx/audio    (gdx/gdx-audio)
         :ctx/files    (gdx/gdx-files)
         :ctx/graphics (gdx/gdx-graphics)
         :ctx/input    (gdx/gdx-input)))
