(ns clojure.gdx.graphics.get-gl20
  (:import (com.badlogic.gdx Graphics)))

(defn f [graphics]
  (Graphics/.getGL20 graphics))
