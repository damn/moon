(ns moon.viewport
  (:require [clj.api.com.badlogic.gdx.utils.viewport :as viewport])
  (:import (com.badlogic.gdx.math Vector2)))

(defn unproject [viewport [x y]]
  (let [^Vector2 v2 (viewport/unproject viewport (Vector2. x y))]
    [(.x v2)
     (.y v2)]))
