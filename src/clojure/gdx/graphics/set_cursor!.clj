(ns clojure.gdx.graphics.set-cursor!
  (:import (com.badlogic.gdx Graphics)
           (com.badlogic.gdx.graphics Cursor)))

(defn f [graphics ^Cursor cursor]
  (Graphics/.setCursor graphics cursor))
