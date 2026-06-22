(ns gdl.set-processor
  (:import (com.badlogic.gdx Input)))

(defn f [^Input input processor]
  (.setInputProcessor input processor))
