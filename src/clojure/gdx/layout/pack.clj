(ns clojure.gdx.layout.pack
  (:import (com.badlogic.gdx.scenes.scene2d.utils Layout)))

(defn f [^Layout layout]
  (Layout/.pack layout))
