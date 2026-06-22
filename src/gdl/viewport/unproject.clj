(ns gdl.viewport.unproject
  (:require [gdl.vector2 :refer [->clj]])
  (:import (com.badlogic.gdx.utils.viewport Viewport)
           (com.badlogic.gdx.math Vector2)))

(defn f [^Viewport viewport [x y]]
  (-> viewport
      (.unproject (Vector2. x y))
      ->clj))
