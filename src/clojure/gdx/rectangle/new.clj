(ns clojure.gdx.rectangle.new
  (:import (com.badlogic.gdx.math Rectangle)))

(defn f [x y width height]
  (Rectangle. (float x) (float y) (float width) (float height)))
