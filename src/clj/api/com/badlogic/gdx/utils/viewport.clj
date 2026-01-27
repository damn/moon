(ns clj.api.com.badlogic.gdx.utils.viewport
  (:import (com.badlogic.gdx.math Vector2) ; TODO grep, clj/clojurize
           (com.badlogic.gdx.utils.viewport FitViewport)))

(defn unproject [^FitViewport viewport [x y]]
  (let [v2 (.unproject viewport (Vector2. x y))]
    [(.x v2)
     (.y v2)]))
