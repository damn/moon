(ns input.set-processor
  (:require [com.badlogic.gdx.input :as input]))

(defn f [input processor]
  (input/set-input-processor! input processor))
